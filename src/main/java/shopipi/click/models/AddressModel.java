package shopipi.click.models;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Data
@Builder
public class AddressModel {
  private String name;
  private String phone;
  @NotEmpty(message = "province not null or empty")
  private String province;
  @NotEmpty(message = "district not null or empty")
  private String district;
  @NotEmpty(message = "ward not null or empty")
  private String address;

  @Default
  private Boolean isDefault = false;
}
