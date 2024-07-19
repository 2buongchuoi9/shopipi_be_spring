package shopipi.click.models;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Category;
import shopipi.click.entity.Sale;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.utils._enum.ProductState;

@Data

public class ProductViewModel {

  private String id;
  private String name;
  private String slug;
  private String thumb;
  private String video;
  private List<String> images;

  // mã giảm giá của sản phẩm cho toàn bộ biến thể
  // tinhd theo % hoặc số tiền cố định

  private Sale sale;

  private String type; // ProductTypeEnum.ELECTRONIC.name()
  private String description;

  private Integer quantity;

  private Double ratingAvg;

  private long totalRating;

  private Long totalComment;

  private Boolean isDeleted;

  private Attribute attribute;

  private List<Variant> variants;

  private Category category;

  // Quản lý trạng thái sản phẩm (PENDING, ACTIVE, DEACTIVE)

  private String state;

  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

  private User shop;

  public ProductViewModel() {

  }
}
