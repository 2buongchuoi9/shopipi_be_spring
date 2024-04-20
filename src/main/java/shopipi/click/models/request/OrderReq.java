package shopipi.click.models.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.utils._enum.TypePayment;

@Data
@Builder
public class OrderReq {
  @NotEmpty(message = "address not null")
  private String address;
  @Default
  private String payment = TypePayment.CASH.name();
  private @Valid List<ShopOrderItemsReq> items;

  @Data
  @Builder
  public static class ShopOrderItemsReq {
    @NotEmpty(message = "shopId not null")
    private String shopId;

    private String discountId;

    private @Valid List<CartReq> items;

    // @NotEmpty(message = "productId is required")
    // private String productId;
    // @NotEmpty(message = "attributeId is required")
    // private String attributeId;
    // @NotNull(message = "quantity is required")
    // private Integer quantity;

  }
}
