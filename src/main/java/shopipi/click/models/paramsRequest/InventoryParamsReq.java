package shopipi.click.models.paramsRequest;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryParamsReq implements Serializable {
  private String keySearch;
  private String shopId;

  private String fromDate;
  private String toDate;
}
