package shopipi.click.models.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddDiscountToCartReq implements Serializable {
  @NotEmpty(message = "shopId not null")
  private String shopId;
  @NotEmpty(message = "discountId not null")
  private String discountId;
}
