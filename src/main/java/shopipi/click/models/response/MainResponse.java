package shopipi.click.models.response;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MainResponse<T extends Object> implements Serializable {
  private int code;
  private HttpStatus status;
  private String message;
  private T data;

  public MainResponse(HttpStatus status, String message) {
    this.status = status;
    this.code = status.value();
    this.message = message;
    this.data = null;
  }

  public MainResponse(HttpStatus status, String message, T data) {
    this.status = status;
    this.code = status.value();
    this.message = message;
    this.data = data;
  }

  public static <T> MainResponse<T> oke(String message, T data) {
    return new MainResponse<>(HttpStatus.OK, message, data);
  }

  public static <T> MainResponse<T> oke(T data) {
    return new MainResponse<>(HttpStatus.OK, "success", data);
  }

}
