package shopipi.click.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.StateDiscount;
import shopipi.click.utils._enum.TypeDiscount;

@Document(collection = "Sales")
@Data
@Builder
public class Sale {
  @Id
  private String id;
  private String name;
  private List<String> productIds;
  private String shopId;
  @Default
  private String type = TypeDiscount.FIXED_AMOUNT.name();
  @Default
  private Double value = 0.0;

  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateStart;
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateEnd;

  // Trường không lưu vào database
  private transient String state;

  public Sale setStateBasedOnDates() {
    LocalDateTime now = LocalDateTime.now();
    if (dateEnd != null && dateEnd.isBefore(now)) {
      this.state = StateDiscount.EXPIRED.name();
    } else if (dateStart != null && dateStart.isAfter(now)) {
      this.state = StateDiscount.NOT_YET_ACTIVE.name();
    } else {
      this.state = StateDiscount.ACTIVE.name();
    }

    return this;
  }

  public Sale removeProductId(String productId) {
    if (this.productIds != null && this.productIds.contains(productId))
      this.productIds.remove(productId);
    return this;
  }

}
