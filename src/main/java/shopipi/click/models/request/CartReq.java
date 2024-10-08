package shopipi.click.models.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class CartReq implements Serializable {
  @NotEmpty(message = "productId is required")
  private String productId;
  @NotEmpty(message = "variantId is required")
  private String variantId;
  @NotNull(message = "quantity is required")
  private Integer quantity;

}
