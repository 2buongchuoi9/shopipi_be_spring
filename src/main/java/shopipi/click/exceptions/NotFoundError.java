package shopipi.click.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundError extends RuntimeException {
  public NotFoundError(String message) {
    super(message);
  }

  public NotFoundError(String objectName, Object obj) {
    super("Could not find " + objectName + " with '" + obj + "' :(");
  }

}