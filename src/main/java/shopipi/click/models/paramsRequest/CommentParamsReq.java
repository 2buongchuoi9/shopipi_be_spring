package shopipi.click.models.paramsRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentParamsReq {
  private String productId;
  private String parentId;
  private String userId;
  private String shopId;
}
