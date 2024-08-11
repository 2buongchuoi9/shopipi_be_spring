package shopipi.click.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Data
@Builder
public class RegisterReq {
  @NotEmpty(message = "Name is required")
  private String name;

  @NotEmpty(message = "Email is required")
  @Pattern(regexp = WebMvcConfig.regexpEmail, message = "Invalid email format")
  private String email;
  @NotEmpty(message = "Password is required")
  @Min(value = 3, message = "Password must be at least 3 characters")
  private String password;

  // @NotEmpty(message = "Address is required")
  // private String address;

  private String image;
}
