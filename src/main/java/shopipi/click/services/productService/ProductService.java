package shopipi.click.services.productService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import shopipi.click.entity.Inventory;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Attribute.ListObjectMap;
import shopipi.click.entity.productSchema.Attribute.ObjectMap;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.ProductParamsReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.models.request.UpdateToggleReq;
import shopipi.click.repositories.CategoryRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.VariantRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepo productRepo;
  private final CategoryRepo cateRepo;
  private final MongoTemplate mongoTemplate;
  private final IUpdateProduct iUpdateProduct;
  private final VariantRepo variantRepo;

  @CacheEvict(value = "product", allEntries = true)
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

  @CacheEvict(value = "product", allEntries = true)
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

  @Cacheable(value = "product", key = "#root.methodName + '-' + #params?.keySearch+ '-' + #params?.categoryId+ '-' + #params?.isDeleted+ '-' + #params?.state+ '-' + #params?.shopId+ '-' + #params?.minPrice+ '-' + #params?.maxPrice+ '-' + #params?.rate + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
  public PageCustom<Product> findProduct(Pageable pageable, ProductParamsReq params) {
    String shopId = params.getShopId();
    String categoryId = params.getCategoryId();
    Double minPrice = params.getMinPrice();
    Double maxPrice = params.getMaxPrice();
    Double rate = params.getRate();
    String keySearch = params.getKeySearch();

    Query query = new Query();

    if (keySearch != null && !keySearch.isEmpty()) {
      String regexPattern = ".*" + keySearch.trim() + ".*";
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("name").regex(regexPattern, "i"),
          Criteria.where("description").regex(regexPattern, "i")));
    }

    if (rate != null) {
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("ratingAvg").gte(rate),
          Criteria.where("ratingAvg").is(Double.parseDouble("0"))));
    }

    if (shopId != null && !shopId.isEmpty()) {
      System.out.println("shopId:::::" + shopId);
      query.addCriteria(Criteria.where("shop.id").is(shopId));
    }

    if (params.getState() != null && !params.getState().isEmpty()) {
      query.addCriteria(Criteria.where("state").is(params.getState()));
    }

    // if (categoryId != null && !categoryId.isEmpty()) {
    // // Tìm kiếm theo categoryId trong cả category.id và category.parentIds
    // query.addCriteria(new Criteria().orOperator(
    // Criteria.where("category.id").is(categoryId),
    // Criteria.where("category.parentIds").in(categoryId)));
    // }

    if (categoryId != null && !categoryId.isEmpty()) {
      // Tìm tất cả các Category có id hoặc parentId giống với categoryId truyền vào
      // List<Category> categories = cateRepo.findByIdOrParentId(categoryId);
      List<Category> categories = findAllSubCategories(categoryId);

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

    if (minPrice != null && maxPrice != null)
      query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
    else if (minPrice != null)
      query.addCriteria(Criteria.where("price").gte(minPrice));
    else if (maxPrice != null)
      query.addCriteria(Criteria.where("price").lte(maxPrice));

    // get total product
    Long total = productRepo.count();

    //
    query.with(pageable);
    List<Product> list = mongoTemplate.find(query, Product.class);

    // list.forEach(product -> {

    // Double minPriceSale = product.getVariants().stream()
    // .map(Variant::getPriceSale)
    // .filter(Objects::nonNull) // Loại bỏ giá null nếu có
    // .min(Double::compareTo)
    // .orElse(null);
    // if (minPriceSale != null) {
    // product.setPrice(minPriceSale); // Cập nhật thuộc tính price của sản phẩm
    // productRepo.save(product);
    // }
    // });

    System.out.println("ccc" + list.size());

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

  @Cacheable(value = "product", key = "#slug")
  public Product findBySlug(String slug) {
    Product product = productRepo.findBySlug(slug).orElseThrow(() -> new BabRequestError("Product not found"));

    return product;
  }

  @Cacheable(value = "product", key = "#id")
  public Product findById(String id) {
    Product product = productRepo.findById(id).orElseThrow(() -> new BabRequestError("Product not found"));

    return product;
  }

  @CacheEvict(value = "product", allEntries = true)
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

  @CacheEvict(value = "product", allEntries = true)
  public Boolean deleteProduct(String id) {
    Product product = productRepo.findById(id).orElseThrow(() -> new NotFoundError("id", id));

    // trước khi xóa sản phẩm cần xóa variant
    try {
      mongoTemplate.remove(new Query(Criteria.where("productId").is(id)), Variant.class);

      // xóa lịch sử nhập hàng
      mongoTemplate.remove(new Query(Criteria.where("productId").is(id)), Inventory.class);

      // cần xóa sản phẩm ra khỏi sale

      productRepo.delete(product);
      return true;
    } catch (Exception e) {
      throw new BabRequestError("Xảy ra lỗi khi xóa sản phẩm");
    }
  }

  public List<Long> countProduct(String shopId) {
    Query query = new Query();
    query.addCriteria(Criteria.where("shop.id").is(shopId));
    // Chỉ lấy trường 'id'
    query.fields().include("id", "sold");

    List<Product> products = mongoTemplate.find(query, Product.class);

    List<String> ids = products.stream().map(Product::getId)
        .collect(Collectors.toList());

    // đếm biến thể dựa vào id sản phẩm
    long countVariant = mongoTemplate.count(new Query().addCriteria(Criteria.where("productId").in(ids)),
        Variant.class);
    long countProduct = ids.size();
    long countSold = products.stream().mapToLong(Product::getSold).sum();

    return Arrays.asList(countProduct, countVariant, countSold);
  }

  private List<Category> findAllSubCategories(String categoryId) {
    List<Category> allCategories = new ArrayList<>();
    findAllSubCategoriesHelper(categoryId, allCategories);
    return allCategories;
  }

  private void findAllSubCategoriesHelper(String categoryId, List<Category> allCategories) {
    Optional<Category> categoryOpt = cateRepo.findById(categoryId);
    if (categoryOpt.isPresent()) {
      Category category = categoryOpt.get();
      allCategories.add(category);
    }
    List<Category> subCategories = cateRepo.findByParentIds(categoryId);
    allCategories.addAll(subCategories);
    for (Category subCategory : subCategories) {
      findAllSubCategoriesHelper(subCategory.getId(), allCategories);
    }
  }

}
