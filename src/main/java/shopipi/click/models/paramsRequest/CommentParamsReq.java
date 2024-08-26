package shopipi.click.models.paramsRequest;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentParamsReq implements Serializable {
  private String productId;
  private String parentId;
  private String userId;
  private String shopId;
}
