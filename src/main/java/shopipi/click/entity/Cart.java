package shopipi.click.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Builder.Default;
import shopipi.click.models.ShopOrderItemsModel;
import lombok.Data;

@Document("Carts")
@Data
@Builder
public class Cart implements Serializable {
  @Id
  private String id;

  @Indexed(unique = true)
  private String userId;

  @Default
  private List<ShopOrderItemsModel> shopOrderItems = new ArrayList<>();

  @Default
  private Double totalDiscount = 0.0;
  private Double total;

  // total price of all items in the cart and discount if any
  // private Double total;

}

/**
 * {
 * "id": "1",
 * "userId": "1",
 * "shopOrderItems":
 * [
 * {
 * "shopId": "1",
 * "discountId": "1",
 * "items": {
 * "productId": "1",
 * "attributeId": "1",
 * "quantity": 1
 * }
 * }
 * }
 */
