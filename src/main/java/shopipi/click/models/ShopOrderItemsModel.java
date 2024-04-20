package shopipi.click.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;

@Data
@Builder
public class ShopOrderItemsModel {
  private String shopId;
  private String discountId;
  @Default
  private List<ProductItemsModel> items = new ArrayList<>();

  @Default
  private Double totalDiscount = 0.0;
  private Double total;

  @Data
  @Builder
  public static class ProductItemsModel {
    private Product product;
    private Attribute attribute;
    private Integer quantity;
    private Double price;
  }

}
