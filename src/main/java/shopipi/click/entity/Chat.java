package shopipi.click.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
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
}
