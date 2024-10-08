package shopipi.click.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Document(collection = "Ratings")
@Data
@Builder
public class Rating implements Serializable {
  @Id
  private String id;
  private String productId;
  private String variantId;
  private String shopId;

  private int value;

  private String comment;

  private List<String> images;

  @CreatedBy
  private User user;

  @Default
  private Boolean isComment = false;

  @Default
  private List<String> likes = new ArrayList();
  private Integer left;
  private Integer right;
  private String parentId;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime updatedAt;
}
