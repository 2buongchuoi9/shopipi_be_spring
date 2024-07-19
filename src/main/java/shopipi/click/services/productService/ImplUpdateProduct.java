package shopipi.click.services.productService;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Inventory;
import shopipi.click.entity.Sale;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.repositories.CommentRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.VariantRepo;
import shopipi.click.utils._enum.StateDiscount;
import shopipi.click.utils._enum.TypeDiscount;

@Component
@RequiredArgsConstructor
public class ImplUpdateProduct implements IUpdateProduct {
  private final ProductRepo productRepo;
  private final CommentRepo commentRepo;
  private final VariantRepo variantRepo;

  // update quantity in product and save to database
  @Override
  public Product inventory(Product product) {
    // get all valueVariant of product and sum quantity
    if (product.getVariants() == null)
      return product;
    int quantity = product.getVariants().stream()
        .mapToInt(Variant::getQuantity)
        .sum();
    product.setQuantity(quantity);
    return productRepo.save(product);
  }

  @Override
  public Product inventory(Product product, Inventory inventory) {
    // get all valueVariant of product and sum quantity
    if (product.getVariants() == null)
      return product;

    List<Variant> variants = variantRepo.findByProductId(product.getId());
    List<Inventory.VariantInventory> variantInventories = inventory.getVariantInventory();

    // update quantity of variant
    variants = variants.stream().map(v -> {
      Inventory.VariantInventory variantInventory = variantInventories.stream()
          .filter(vi -> vi.getVariantId().equals(v.getId())).findFirst().orElse(null);
      if (variantInventory == null)
        return v;
      v.setQuantity(v.getQuantity() + variantInventory.getQuantity());
      v.setPriceImport(variantInventory.getPriceImport() == 0 ? v.getPriceImport() : variantInventory.getPriceImport());
      return variantRepo.save(v);
    }).toList();

    // update quantity of product
    product.setVariants(variants);
    return inventory(product);
  }

  // update totalComment in product and save to database
  @Override
  public Product totalComment(Product product) {
    product.setTotalComment(commentRepo.countByProductId(product.getId()));
    return productRepo.save(product);
  }

  /**
   * @type: ADD, UPDATE, DELETE
   * @value: if update value = newValue - oldValue
   */
  @Override
  public Product totalRatingAndRatingAvg(int type, Product product, int value) {
    System.out.println("value: " + value);
    // count total rating
    long totalRating = product.getTotalRating() + type;

    // average rating
    double ratingAvg = (product.getRatingAvg() * product.getTotalRating() + (type == DELETE ? value * -1 : value))
        / totalRating;

    product.setTotalRating(totalRating);
    product.setRatingAvg(ratingAvg);

    return productRepo.save(product);
  }

  @Override
  public Product priceSaleProduct(Product product, Sale sale) {

    // find list variant of product
    if (product.getVariants() == null)
      return product;
    List<Variant> variants = variantRepo.findByProductId(product.getId());

    // if sale is null, set priceSale = price
    // hành động xóa khuyến mãi
    if (sale == null) {
      variants = variants.stream().map(v -> {
        v.setPriceSale(v.getPrice());
        return variantRepo.save(v);
      }).toList();
      product.setVariants(variants);
      product.setSale(null);
      return productRepo.save(product);
    }

    // check sale expired
    sale.setStateBasedOnDates();

    // update price of variant
    if (!sale.getState().equals(StateDiscount.ACTIVE.name()))
      return product;

    variants = variants.stream().map(v -> {
      Double priceSale = sale.getType().equals(TypeDiscount.FIXED_AMOUNT.name()) ? v.getPrice() - sale.getValue()
          : v.getPrice() - (v.getPrice() * sale.getValue() / 100);

      v.setPriceSale(priceSale);

      // save variant
      return variantRepo.save(v);
    }).toList();

    // save product
    product.setVariants(variants);
    product.setSale(sale);
    return productRepo.save(product);
  }

}
