package shopipi.click.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Document(collection = "OnlineStatusUsers")
@Data
@Builder
public class OnlineStatusUser {
  @Id
  private String id;

  private String userId;

  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime time;
  private Boolean isOnline;
}
