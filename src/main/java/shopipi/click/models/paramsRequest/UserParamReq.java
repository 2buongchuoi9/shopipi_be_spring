package shopipi.click.models.paramsRequest;

import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.validation.constraints.AssertTrue;
import lombok.Builder;
import lombok.Data;
import shopipi.click.utils._enum.AuthTypeEnum;

@Data
@Builder
public class UserParamReq {

  private String keySearch;
  private Boolean status;
  private String authType;
  private Boolean verify;

  @AssertTrue(message = "The authType must match the pattern")
  private boolean isValidAuthType() {
    if (authType == null)
      return true;

    return Arrays.stream(AuthTypeEnum.values())
        .map(Enum::name)
        .collect(Collectors.toList())
        .contains(authType);
  }

}
