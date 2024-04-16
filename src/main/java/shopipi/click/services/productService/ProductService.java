package shopipi.click.services.productService;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.models.request.AttributeReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.repositoryUtil.IUpdateInventoryProduct;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
public class ProductService implements IUpdateInventoryProduct {
  private final ProductFactory productFactory;
  private final ProductRepo productRepo;
  private final MongoTemplate mongoTemplate;

  public Product addProduct(ProductReq productReq) {
    // create product
    Product product = productRepo.save(
        Product.builder().name(productReq.getName())
            .thumb(productReq.getThumb())
            .images(productReq.getImages())
            .price(productReq.getPrice())
            .priceImport(productReq.getPriceImport())
            .type(productReq.getType())
            .description(productReq.getDescription())
            .status(productReq.getStatus())
            .build());

    // create Attribute
    if (productReq.getAttributes() != null) {
      product.setAttributes(productFactory.createAttributes(product, productReq.getType(), productReq.getAttributes()));
    }
    return updateInventoryProduct(product);
  }

  public Product addProductAttribute(String productId, AttributeReq attributesReq) {
    if (attributesReq.getAttributes() == null || attributesReq.getAttributes().isEmpty()
        || attributesReq.getAttributes().size() == 0)
      throw new BabRequestError("Attributes is required");

    Product product = productRepo.findById(productId).orElseThrow(() -> new BabRequestError("Product not found"));
    List<Attribute> newAttributes = productFactory.createAttributes(product, attributesReq.getType(),
        attributesReq.getAttributes());
    product.pushAttributes(newAttributes);
    return updateInventoryProduct(product);
  }

  public PageCustom<Product> findProduct(Pageable pageable) {
    Query query = new Query();
    // get total product
    Long total = productRepo.count();
    // // get product with pagination
    query.with(pageable);
    List<Product> list = mongoTemplate.find(query, Product.class);
    return new PageCustom<Product>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  // update quantity in product of database
  @Override
  public Product updateInventoryProduct(String productId) {
    Product product = productRepo.findById(productId).orElseThrow(() -> new BabRequestError("Product not found"));
    return updateInventoryProduct(product);
  }

  // update quantity in product and save to database
  @Override
  public Product updateInventoryProduct(Product product) {
    // get all attribute of product and sum quantity
    if (product.getAttributes() == null)
      return product;
    int quantity = product.getAttributes().stream()
        .mapToInt(Attribute::getQuantity)
        .sum();
    product.setQuantity(quantity);
    return productRepo.save(product);
  }

}
