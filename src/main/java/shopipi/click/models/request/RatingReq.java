package shopipi.click.models.request;

import java.util.List;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class RatingReq {
  @NotEmpty(message = "ProductId is required")
  private String productId;

  @NotNull(message = "isComment is required")
  private Boolean isComment;

  @Default
  private String variantId = null;

  @NotEmpty(message = "content is not null or empty")
  private String comment;

  private String parentId;

  private List<String> images;

  @Default
  private int value = 0;

  @AssertTrue(message = "Rating value must be greater than 0")
  public boolean isRatingValueValid() {
    if (isComment != null && !isComment) {
      return value > 0 && value <= 10;
    }
    return true;
  }

  @AssertTrue(message = "VariantId is required")
  public boolean isVariantId() {
    if (isComment != null && !isComment) {
      return variantId != null;
    }
    return true;
  }

}
