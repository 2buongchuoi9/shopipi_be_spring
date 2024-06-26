package shopipi.click.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Document(collection = "Ratings")
@Data
@Builder
public class Rating {
  @Id
  private String id;
  private String productId;
  private String shopId;

  private int value;

  @CreatedBy
  private User user;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createAt;

  @LastModifiedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime updateAt;
}
