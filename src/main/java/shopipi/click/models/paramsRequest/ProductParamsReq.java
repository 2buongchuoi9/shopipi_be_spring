package shopipi.click.models.paramsRequest;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductParamsReq implements Serializable {
  private String keySearch;

  private String categoryId;

  private Boolean isDeleted;

  private String state;

  private String shopId;

  private Double minPrice;

  private Double maxPrice;

  private Double rate;
}
