package shopipi.click.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Category;
import shopipi.click.entity.Sale;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Variant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Products")
public class ProductDTO {
  @Id
  private String id;
  private String name;
  private String slug;
  private String thumb;
  private String video;
  @Default
  private List<String> images = new ArrayList<>();
  private Sale sale;
  private String type;
  private String description;
  private Integer quantity;
  private Double ratingAvg;
  private long totalRating;
  private Long totalComment;
  private Boolean isDeleted;
  private Attribute attribute;
  @Default
  private List<Variant> variants = new ArrayList<>();
  private Integer sold;

  private Category category;
  private String state;
  private LocalDateTime createdAt;
  private User shop;
  private Double minPrice;

  // private String id;
  // private String name;
  // private String slug;
  // private String thumb;
  // private String video;
  // private List<String> images; // Đảm bảo kiểu dữ liệu đúng
  // private Double sale;
  // private String type;
  // private String description;
  // private Integer quantity;
  // private Double ratingAvg;
  // private Integer totalRating;
  // private Integer totalComment;
  // private Boolean isDeleted;
  // private Map<String, Object> attribute; // Hoặc kiểu dữ liệu đúng
  // private List<Variant> variants; // Đảm bảo kiểu dữ liệu đúng
  // private Integer sold;
  // private Category category;
  // private String state;
  // private LocalDateTime createdAt;
  // private User shop;
  // private Double minPrice;
}
