package shopipi.click.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.ChatType;

@Document(collection = "Chats")
@Data
@Builder
public class Chat {
  @Id
  private String id;
  private String senderId;
  private String receiverId;
  private String message;
  // 0: text, 1: image, 2: file
  @Default
  private String type = ChatType.TEXT.name();
  @Default
  private Boolean isRead = false;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

}
