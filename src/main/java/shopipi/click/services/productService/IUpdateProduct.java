package shopipi.click.services.productService;

import org.springframework.stereotype.Component;

import shopipi.click.entity.Inventory;
import shopipi.click.entity.Sale;
import shopipi.click.entity.productSchema.Product;

@Component
public interface IUpdateProduct {
  // danger!!! all type don't change
  public static final int ADD = 1;
  public static final int UPDATE = 0;
  public static final int DELETE = -1;

  // update quantity in product and save to database
  public Product inventory(Product product);

  public Product inventory(Product product, Inventory inventory);

  // update totalComment in product and save to database
  public Product totalComment(Product product);

  public Product priceSaleProduct(Product product, Sale sale);

  /**
   * @type: ADD, UPDATE, DELETE
   * @value: if update value = newValue - oldValue
   */
  public Product totalRatingAndRatingAvg(int type, Product product, int value);

  default Product afterAddRating(Product product, int value) {
    return totalRatingAndRatingAvg(ADD, product, value);
  }

  default Product afterUpdateRating(Product product, int value, int oldValue) {
    return totalRatingAndRatingAvg(UPDATE, product, value + (oldValue * -1));
  }

  default Product afterDeleteRating(Product product, int oldValue) {
    return totalRatingAndRatingAvg(DELETE, product, oldValue);
  }

}
