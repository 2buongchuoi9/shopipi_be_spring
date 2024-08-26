package shopipi.click.models.request;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.TypeDiscount;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleReq implements Serializable {
  private String name;
  @NotEmpty(message = "shopId is required")
  private String shopId;
  @Size(min = 1, message = "The list must contain at least 1 product IDs")
  @NotNull(message = "productIds is required")
  private List<String> productIds;

  private Double value;

  @Pattern(regexp = "FIXED_AMOUNT|PERCENTAGE_AMOUNT", message = "type must be FIXED_AMOUNT or PERCENTAGE_AMOUNT")
  @Default
  private String type = TypeDiscount.FIXED_AMOUNT.name();

  @NotNull(message = "dateStart is required")
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateStart;

  @NotNull(message = "dateEnd is required")
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateEnd;

  // check if type is PERCENTAGE_AMOUNT then value must be between 0 and 100
  @AssertTrue(message = "giá trị phải nằm trong khoảng 0 và 100 nếu type là giảm theo %, và lớn hơn 5000 nếu giảm theo số tiền")
  public boolean isValueValid() {
    if (type.equalsIgnoreCase(TypeDiscount.FIXED_AMOUNT.name()))
      return value > 5000.0;

    return value >= 0.0 && value <= 100.0;
  }
}
