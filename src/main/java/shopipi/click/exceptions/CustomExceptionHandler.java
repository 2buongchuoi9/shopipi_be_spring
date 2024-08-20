package shopipi.click.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import shopipi.click.models.response.MainResponse;

@RestControllerAdvice

@SuppressWarnings("rawtypes")
public class CustomExceptionHandler {

  @ExceptionHandler(NotFoundError.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<MainResponse> handlerNotFoundError(NotFoundError ex) {
    return new ResponseEntity<>(new MainResponse(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BabRequestError.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<MainResponse> handlerBadRequestError(BabRequestError ex) {
    return new ResponseEntity<>(new MainResponse(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DuplicateRecordError.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<MainResponse> handlerDuplicateRecordException(DuplicateRecordError ex) {
    return new ResponseEntity<>(new MainResponse(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoAuthorizeError.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<MainResponse> handlerUnauthorizeError(NoAuthorizeError ex) {
    return new ResponseEntity<>(new MainResponse(HttpStatus.UNAUTHORIZED,
        ex.getMessage()),
        HttpStatus.UNAUTHORIZED);

  }

  @SuppressWarnings("unchecked")
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  ResponseEntity<MainResponse> handleAccessDeniedException(AccessDeniedException ex) {
    return new ResponseEntity<>(
        new MainResponse(HttpStatus.FORBIDDEN, "You are not have permission.", ex.getMessage()),
        HttpStatus.FORBIDDEN);
  }

  @SuppressWarnings("unchecked")
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<MainResponse> handleValidationException(MethodArgumentNotValidException ex) {
    List<ObjectError> errors = ex.getBindingResult().getAllErrors();
    Map<String, String> map = new HashMap<>(errors.size());
    errors.forEach((error) -> {
      String key = ((FieldError) error).getField();
      String val = error.getDefaultMessage();
      map.put(key, val);
    });
    return new ResponseEntity<>(
        new MainResponse(HttpStatus.BAD_REQUEST, "validate error", map),
        HttpStatus.BAD_REQUEST);
  }

  // Xử lý tất cả các exception chưa được khai báo
  @ExceptionHandler(Exception.class)
  public ResponseEntity<MainResponse> handlerException(Exception ex) {
    MainResponse err = new MainResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        ex.getMessage());

    return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
