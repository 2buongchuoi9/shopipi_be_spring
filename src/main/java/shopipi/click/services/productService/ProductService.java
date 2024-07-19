package shopipi.click.services.productService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Category;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.entity.productSchema.Attribute.ListObjectMap;
import shopipi.click.entity.productSchema.Attribute.ObjectMap;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.ProductParamsReq;
import shopipi.click.models.request.AttributeReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.models.request.UpdateToggleReq;
import shopipi.click.repositories.CategoryRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.VariantRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.utils._enum.ProductState;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductFactory productFactory;
  private final ProductRepo productRepo;
  private final CategoryRepo cateRepo;
  private final MongoTemplate mongoTemplate;
  private final IUpdateProduct iUpdateProduct;
  private final VariantRepo variantRepo;

  public Product addProduct(ProductReq productReq) {

    // check found product
    if (productRepo.existsByName(productReq.getName()))
      throw new BabRequestError("Product name is exist");

    // check category
    Category category = cateRepo.findById(productReq.getCategoryId())
        .orElseThrow(() -> new BabRequestError("Category not found"));

    List<Variant> variants = productReq.getVariants();
    Attribute attribute = productReq.getAttribute();

    if (!validateVariants(variants, attribute)) {
      throw new BabRequestError("Invalid variants and attribute! Please check again.");
    }

    // create product
    Product product = productRepo.save(
        Product.builder()
            .name(productReq.getName())
            .slug(productReq.getSlug())
            .thumb(productReq.getThumb())
            .video(productReq.getVideo())
            .images(productReq.getImages())
            // .price(productReq.getPrice())
            // .priceImport(productReq.getPriceImport())
            .type(productReq.getType())
            .description(productReq.getDescription())
            .state(productReq.getState())
            .isDeleted(productReq.getIsDeleted())
            .category(category)
            .attribute(productReq.getAttribute())
            // .variants(productReq.getVariants())
            .build());

    Product productSave = productRepo.save(product);

    productSave.setVariants(variants.stream().map(variant -> {
      variant.setProductId(productSave.getId());
      variant = variantRepo.save(Variant.builder()
          .productId(productSave.getId())
          .valueVariant(variant.getValueVariant())
          // tạo sản phẩm, số lượng mặc định là 0, giá nhập hàng là 0
          // muốn cập nhật số lượng thì phải qua inventory
          // .quantity(variant.getQuantity())
          .price(variant.getPrice())
          .priceImport(0.0)
          .priceSale(variant.getPrice())
          .quantity(0)
          .build());
      return variant;
    }).toList());

    return iUpdateProduct.inventory(product); // in inventory will save product
  }

  public Product updateProduct(String productId, ProductReq productReq) {
    Product product = productRepo.findById(productId)
        .orElseThrow(() -> new BabRequestError("Product not found"));

    if (productRepo.existsByNameAndIdNot(productReq.getName(), productId))
      throw new DuplicateKeyException("Product name is exist: " + productReq.getName());

    // check category
    Category category = cateRepo.findById(productReq.getCategoryId())
        .orElseThrow(() -> new BabRequestError("Category not found"));

    List<Variant> variants = productReq.getVariants();
    Attribute attribute = productReq.getAttribute();

    if (!validateVariants(variants, attribute)) {
      throw new BabRequestError("Invalid variants and attribute! Please check again.");
    }

    product.setName(productReq.getName());
    product.setSlug(productReq.getSlug());
    product.setThumb(productReq.getThumb());
    product.setVideo(productReq.getVideo());
    product.setImages(productReq.getImages());
    // product.setPrice(productReq.getPrice());
    // product.setPriceImport(productReq.getPriceImport());
    product.setType(productReq.getType());
    product.setDescription(productReq.getDescription());
    product.setState(productReq.getState());
    product.setIsDeleted(productReq.getIsDeleted());
    product.setCategory(category);
    product.setAttribute(productReq.getAttribute());

    Product productSave = productRepo.save(product);

    variants = variants.stream().map(variant -> {
      if (variant.getId() == null) {
        variant.setProductId(productSave.getId());
        variant.setPrice(variant.getPrice());
        return variantRepo.save(variant);
      } else {
        Variant variantUpdate = variantRepo.findById(variant.getId())
            .orElseThrow(() -> new BabRequestError("Variant not found"));
        variantUpdate.setValueVariant(variant.getValueVariant());
        variantUpdate.setPrice(variant.getPrice());
        return variantRepo.save(variantUpdate);
      }
    }).toList();

    productSave.setVariants(variants);

    return iUpdateProduct.inventory(product);
  }

  public PageCustom<Product> findProduct(Pageable pageable, ProductParamsReq params) {
    String shopId = params.getShopId();
    String categoryId = params.getCategoryId();

    Query query = new Query();

    if (shopId != null && !shopId.isEmpty()) {
      System.out.println("shopId:::::" + shopId);
      query.addCriteria(Criteria.where("shop.id").is(shopId));
    }

    if (params.getState() != null && !params.getState().isEmpty()) {
      query.addCriteria(Criteria.where("state").is(params.getState()));
    }

    if (categoryId != null && !categoryId.isEmpty()) {
      if (categoryId != null && !categoryId.isEmpty()) {
        // Tìm tất cả các Category có id hoặc parentId giống với categoryId truyền vào
        List<Category> categories = cateRepo.findByIdOrParentId(categoryId);

        categories.stream().forEach(System.out::println);

        // Lấy danh sách id của các Category tìm được chuyển sang objectId
        List<ObjectId> objectIds = categories.stream()
            .map(Category::getId)
            .filter(ObjectId::isValid)
            .map(ObjectId::new)
            .collect(Collectors.toList());
        // Tìm tất cả các Product có categoryId nằm trong danh sách id của Category
        query.addCriteria(Criteria.where("category.$id").in(objectIds));
      }
    }

    // get total product
    Long total = productRepo.count();

    //
    query.with(pageable);
    List<Product> list = mongoTemplate.find(query, Product.class);

    // list.forEach(v -> {
    // mongoTemplate.remove(new
    // Query().addCriteria(Criteria.where("productId").ne(v.getId())),
    // Variant.class);
    // });
    // System.out.println("ccc" + list.size());

    return new PageCustom<Product>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public static boolean validateVariants(List<Variant> variants, Attribute attribute) {
    List<ListObjectMap> variantSchema = attribute.getListVariant();

    // Convert variantSchema to a Map for faster lookup
    Map<String, Set<String>> schemaMap = variantSchema.stream()
        .collect(Collectors.toMap(
            ListObjectMap::getKey,
            lom -> new HashSet<>(lom.getValues())));

    int schemaSize = schemaMap.values().stream().mapToInt(Set::size).reduce(1, (a, b) -> a * b);

    for (Variant variant : variants) {
      List<ObjectMap> valueVariant = variant.getValueVariant();

      // Check if keys are valid
      Set<String> valueVariantKeys = valueVariant.stream()
          .map(ObjectMap::getKey)
          .collect(Collectors.toSet());

      if (!schemaMap.keySet().containsAll(valueVariantKeys)) // Variant contains keys not present in schema
        throw new BabRequestError("Variant contains keys not present in schema");

      // Check if number of variants matches number of schema values
      if (variants.size() != schemaSize) {
        return false;
      }

      // Check if values are valid
      for (ObjectMap valueMap : valueVariant) {
        String key = valueMap.getKey();
        String value = valueMap.getValue();

        Set<String> allowedValues = schemaMap.get(key);
        if (allowedValues == null || !allowedValues.contains(value)) // Variant contains invalid value for key
          throw new BabRequestError("Variant contains invalid value for key");

      }
    }

    return true;
  }

  public Product findBySlug(String slug) {
    Product product = productRepo.findBySlug(slug).orElseThrow(() -> new BabRequestError("Product not found"));

    return product;
  }

  public Product findById(String id) {
    Product product = productRepo.findById(id).orElseThrow(() -> new BabRequestError("Product not found"));

    return product;
  }

  public Boolean updateManyState(UpdateToggleReq req) {
    req.getIds().forEach(id -> {
      // check exit all ids
      if (!productRepo.existsById(id))
        throw new NotFoundError("id", id);
    });

    try {
      mongoTemplate.updateMulti(new Query(Criteria.where("id").in(req.getIds())),
          new Update().set("state", req.getValue()), Product.class);
      return true;

    } catch (Exception e) {
      throw new Error(e.getMessage());
    }
  }

  public Boolean deleteProduct(String id) {
    Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundError("id", id));

    // trước khi xóa sản phẩm cần xóa variant
    try {
      mongoTemplate.remove(new Query(Criteria.where("productId").is(id)), Variant.class);

      // xóa lịch sử nhập hàng
      productRepo.delete(product);
      return true;
    } catch (Exception e) {
      throw new BabRequestError("Xảy ra lỗi khi xóa sản phẩm");
    }
  }

}
