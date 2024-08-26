package shopipi.click.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;

@Document(collection = "Inventory")
@Data
@Builder
public class Inventory implements Serializable {
  @Id
  private String id;
  @NotEmpty(message = "shopId is required")
  private String shopId;
  @NotEmpty(message = "productId is required")
  private String productId;
  private List<VariantInventory> variantInventory;

  // sản phẩm đã xóa nhưng vẫn lưu lịch sử nhập hàng
  @Default
  private Boolean isDeleted = false;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

  @Data
  @Builder
  public static class VariantInventory implements Serializable {
    @NotEmpty(message = "variantId is required")
    private String variantId;
    @NotEmpty(message = "variantName is required")
    private Integer quantity;
    @NotNull(message = "priceImport is required")
    private Double priceImport;
  }

}
