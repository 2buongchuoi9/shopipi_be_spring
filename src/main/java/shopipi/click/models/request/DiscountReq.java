package shopipi.click.models.request;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.TypeDiscount;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountReq {

  private String name;
  private String code;

  @Pattern(regexp = "FIXED_AMOUNT|PERCENTAGE_AMOUNT", message = "type must be FIXED_AMOUNT or PERCENTAGE_AMOUNT")
  @Default
  private String type = TypeDiscount.FIXED_AMOUNT.name();

  @Default
  private Double value = 0.0; // neu type=PERCENTAGE_AMOUNT thi tinh theo %(vidu: 34.5 => 34.5%)
  @Default
  private Integer totalCount = 50; // so luong discount co the su dung
  @Default
  private Double minOrderValue = null; // gia tri toi thieu de ap dung discount

  @Default
  private Integer countUserUseDiscount = 1; // so lan su dung cua moi user
  @Default
  private Boolean status = true;

  @NotNull(message = "dateStart is required")
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateStart;

  @NotNull(message = "dateEnd is required")
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateEnd;

  // check if type is PERCENTAGE_AMOUNT then value must be between 0 and 100
  @AssertTrue(message = "value must be between 0 and 100")
  public boolean isValueValid() {
    if (type.equalsIgnoreCase(TypeDiscount.FIXED_AMOUNT.name()))
      return true;
    return value >= 0.0 && value <= 100.0;
  }

  // check dateEnd must be after dateStart and dateEnd must be future or present
  @AssertTrue(message = "dateEnd must be after dateStart and dateEnd must be future or present")
  public boolean isDateEndValid() {
    return dateEnd.isAfter(dateStart) && dateEnd.isAfter(LocalDateTime.now());
  }

}
