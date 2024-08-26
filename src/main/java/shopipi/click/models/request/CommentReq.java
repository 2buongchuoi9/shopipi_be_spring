package shopipi.click.models.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentReq implements Serializable {
  @NotEmpty(message = "productId is not null or empty")
  private String productId;
  private String parentId;
  @NotEmpty(message = "content is not null or empty")
  private String content;
}
