package shopipi.click.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Inventory;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.InventoryParamsReq;
import shopipi.click.repositories.InventoryRepo;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;

@Service
@RequiredArgsConstructor
public class InventoryService {
  private final InventoryRepo inventoryRepo;
  private final ProductRepo productRepo;
  private final IUpdateProduct updateProduct;
  private final MongoTemplate mongoTemplate;

  // nhập hàng
  @CacheEvict(value = "product", allEntries = true)
  public Product addInventory(Inventory inventory) {

    Product product = productRepo.findById(inventory.getProductId())
        .orElseThrow(() -> new NotFoundError("Product not found"));

    // cập nhật số lượng của variant và product
    return updateProduct.inventory(product, inventoryRepo.save(inventory));

  }

  public List<Product> addManyInventory(List<Inventory> inventory) {
    return inventory.stream().map(this::addInventory).toList();
  }

  public PageCustom<Inventory> findInventory(Pageable pageable, InventoryParamsReq params) {
    Query query = new Query();

    if (params.getShopId() != null && !params.getShopId().isEmpty())
      query.addCriteria(Criteria.where("shopId").is(params.getShopId()));

    long total = mongoTemplate.count(query, Inventory.class);
    List<Inventory> list = mongoTemplate.find(query, Inventory.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }
}
