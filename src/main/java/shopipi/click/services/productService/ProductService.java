package shopipi.click.services.productService;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Category;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.models.request.AttributeReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.repositories.CategoryRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductFactory productFactory;
  private final ProductRepo productRepo;
  private final CategoryRepo cateRepo;
  private final MongoTemplate mongoTemplate;
  private final IUpdateProduct iUpdateProduct;

  public Product addProduct(ProductReq productReq) {
    // check category
    Category category = cateRepo.findById(productReq.getCategoryId())
        .orElseThrow(() -> new BabRequestError("Category not found"));

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
            .category(category)
            .build());

    // create Attribute
    if (productReq.getAttributes() != null) {
      product.setAttributes(productFactory.createAttributes(product, productReq.getType(), productReq.getAttributes()));
    }
    return iUpdateProduct.inventory(product);
  }

  public Product addProductAttribute(String productId, AttributeReq attributesReq) {
    if (attributesReq.getAttributes() == null || attributesReq.getAttributes().isEmpty()
        || attributesReq.getAttributes().size() == 0)
      throw new BabRequestError("Attributes is required");

    Product product = productRepo.findById(productId).orElseThrow(() -> new BabRequestError("Product not found"));
    List<Attribute> newAttributes = productFactory.createAttributes(product, attributesReq.getType(),
        attributesReq.getAttributes());
    product.pushAttributes(newAttributes);
    return iUpdateProduct.inventory(product);
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

}
