package shopipi.click.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Document(collection = "Comments")
@CompoundIndex(def = "{'productId': 1, 'shopId': 1 , 'left': 1, 'right': 1}")
@CompoundIndex(def = "{'productId': 1, 'shopId': 1 , 'left': 1, 'right': 1, 'parentId': 1, 'user': 1}")
@CompoundIndex(def = "{'productId': 1, 'shopId': 1 , 'left': 1, 'right': 1, 'parentId': 1, 'user': 1, 'isDelete': 1}")
@Data
@Builder
public class Comment {
  @Id
  private String id;
  private String productId;
  private String shopId;
  @CreatedBy
  private User user;
  private String content;
  private Integer left;
  private Integer right;
  @Default
  private List<String> likes = new ArrayList();
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
