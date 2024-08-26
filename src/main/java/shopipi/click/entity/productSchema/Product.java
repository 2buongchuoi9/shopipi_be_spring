package shopipi.click.entity.productSchema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Category;
import shopipi.click.entity.Sale;
import shopipi.click.entity.User;
import shopipi.click.utils._enum.ProductState;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Products")
@CompoundIndex(name = "index_status", def = "{'status': 1}")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Serializable {
  @Id
  private String id;
  private String name;
  private String slug;
  private String thumb;
  private String video;
  private List<String> images;

  // mã giảm giá của sản phẩm cho toàn bộ biến thể
  // tinhd theo % hoặc số tiền cố định
  @Default
  private Sale sale = null;

  private String type; // ProductTypeEnum.ELECTRONIC.name()
  private String description;
  @Default
  private Integer quantity = 0;
  @Default
  private Double ratingAvg = 0.0;
  @Default
  private long totalRating = 0;
  @Default
  private Long totalComment = 0l;

  @Default
  private Boolean isDeleted = false;

  private Attribute attribute;

  private List<Variant> variants;

  @Default
  private Integer sold = 0;

  @DBRef
  private Category category;

  @Default
  private Double price = 0.0;

  // Quản lý trạng thái sản phẩm (PENDING, ACTIVE, DEACTIVE)
  @Default
  private String state = ProductState.PENDING.name();

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

  @CreatedBy
  private User shop;

  public Product(String id) {
    this.id = id;
  }

  // public void pushAttributes(List<Attribute> attributes) {
  // // if this attributes is null, create new list
  // if (this.attributes == null) {
  // this.attributes = attributes;
  // return;
  // }
  // this.getAttributes().addAll(attributes);
  // }

}