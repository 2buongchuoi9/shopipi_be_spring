package shopipi.click.models.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordReq {

  @NotEmpty(message = "password not null or empty")
  private String password;
  @NotEmpty(message = "passwordNew not null or empty")
  private String passwordNew;
  @NotEmpty(message = "passwordNewConfirm not null or empty")
  private String passwordNewConfirm;

  @AssertTrue(message = "passwordNew and passwordNewConfirm is not true")
  boolean isValidatePassword() {
    return passwordNew.equals(passwordNewConfirm);
  }
}