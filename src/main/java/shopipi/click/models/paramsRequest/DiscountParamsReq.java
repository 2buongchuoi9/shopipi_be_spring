package shopipi.click.models.paramsRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiscountParamsReq {
  private String shopId;
  private String keySearch;

  private String state;

}
