package shopipi.click.models.paramsRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderParamsReq {
  private String userId;
  private String shopId;
  private String state;
}
