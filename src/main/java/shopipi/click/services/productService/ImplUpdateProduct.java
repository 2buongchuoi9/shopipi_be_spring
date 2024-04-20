package shopipi.click.services.productService;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.repositories.CommentRepo;
import shopipi.click.repositories.ProductRepo;

@Component
@RequiredArgsConstructor
public class ImplUpdateProduct implements IUpdateProduct {
  private final ProductRepo productRepo;
  private final CommentRepo commentRepo;

  // update quantity in product and save to database
  @Override
  public Product inventory(Product product) {
    // get all attribute of product and sum quantity
    if (product.getAttributes() == null)
      return product;
    int quantity = product.getAttributes().stream()
        .mapToInt(Attribute::getQuantity)
        .sum();
    product.setQuantity(quantity);
    return productRepo.save(product);
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

}
