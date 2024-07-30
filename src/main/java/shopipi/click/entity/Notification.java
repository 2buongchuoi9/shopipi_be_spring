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
import shopipi.click.utils._enum.NotificationType;

@Document(collection = "Notifications")
@Data
@Builder
public class Notification {
  @Id
  private String id;

  private String content;

  private String userTo;

  private User userFrom;

  @Default
  private Boolean read = false;

  @Default
  private Boolean delivered = false;

  private NotificationType notificationType;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

}
