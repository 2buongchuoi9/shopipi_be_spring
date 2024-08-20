package shopipi.click.models.paramsRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingParamsReq {
  private String productId;
  private String parentId;
  private String userId;
  private String shopId;

  private Boolean isComment;

  private Integer value;

  private Boolean hasIMage;

}
