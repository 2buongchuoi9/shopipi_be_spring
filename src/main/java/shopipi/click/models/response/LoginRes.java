package shopipi.click.models.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import shopipi.click.entity.User;

@Data
@Builder
@AllArgsConstructor
public class LoginRes implements Serializable {
  private TokenStore Token;
  private User user;

  /**
   * TokenStore
   */
  @AllArgsConstructor
  @Getter
  public static class TokenStore {
    private String accessToken;
    private String refreshToken;

  }
}
