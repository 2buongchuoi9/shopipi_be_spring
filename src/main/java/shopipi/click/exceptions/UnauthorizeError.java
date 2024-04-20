package shopipi.click.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnAuthorizeError extends InsufficientAuthenticationException {
  private String mes;

  public UnAuthorizeError() {
    super("You are not authorized to access this resource.");
    this.mes = "You are not authorized to access this resource.";
  }

  public UnAuthorizeError(String message) {
    super(message);
    this.mes = message;
  }

  @Override
  public String getMessage() {
    return mes;
  }

}
