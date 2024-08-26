package shopipi.click.models.paramsRequest;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaleParamsReq implements Serializable {
  private String shopId;
  private String keySearch;

  private String state;
}
