package shopipi.click.entity.productSchema;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Category;
import shopipi.click.entity.User;
import lombok.Data;

@Document(collection = "Products")
@CompoundIndex(name = "index_status", def = "{'status': 1}")
@Data
@Builder
public class Product {
  @Id
  private String id;
  private String name;
  private String slug;
  private String thumb;
  private List<String> images;
  private Double price;
  private Double priceImport;
  private String type; // ProductTypeEnum.ELECTRONIC.name()
  @Default
  private Integer quantity = 0;
  private String description;
  @Default
  private Double ratingAvg = 0.0;
  @Default
  private long totalRating = 0;
  @Default
  private Long totalComment = 0l;
  @Default
  private Boolean status = true;

  private List<Attribute> attributes;

  private Category category;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createAt;

  @CreatedBy
  private User shop;

  public void pushAttributes(List<Attribute> attributes) {
    // if this attributes is null, create new list
    if (this.attributes == null) {
      this.attributes = attributes;
      return;
    }
    this.getAttributes().addAll(attributes);
  }

}