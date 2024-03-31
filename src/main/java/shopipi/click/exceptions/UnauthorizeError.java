package shopipi.click.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizeError extends InsufficientAuthenticationException {
  private String mes;

  public UnauthorizeError(String message) {
    super(message);
    this.mes = message;
  }

  @Override
  public String getMessage() {
    return mes;
  }

}
