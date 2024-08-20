package shopipi.click.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BabRequestError extends RuntimeException {
  public BabRequestError(String message) {
    super(message);
  }
}
