package shopipi.click.models.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingReq {
  @NotEmpty(message = "ProductId is required")
  private String productId;

  private String comment;

  private List<String> images;

  @NotNull(message = "Rating value is required")
  @Min(value = 1, message = "Rating value must be greater than 0")
  @Max(value = 10, message = "Rating value must be less than 10")
  private int value;
}
