package shopipi.click.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateRecordError extends RuntimeException {
  public DuplicateRecordError(String message) {
    super(message);
  }

  public DuplicateRecordError(String objectName, Object obj) {
    super("Duplicate record " + objectName + " with: '" + obj + "' :(");
  }
}
