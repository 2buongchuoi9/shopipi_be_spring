package shopipi.click.repositories.repositoryUtil;

import shopipi.click.entity.productSchema.Product;

public interface IUpdateInventoryProduct {
  public Product updateInventoryProduct(String productId);

  public Product updateInventoryProduct(Product product);
}
