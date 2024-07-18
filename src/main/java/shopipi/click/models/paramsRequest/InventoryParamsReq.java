package shopipi.click.models.paramsRequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryParamsReq {
  private String keySearch;
  private String shopId;

  private String fromDate;
  private String toDate;
}
