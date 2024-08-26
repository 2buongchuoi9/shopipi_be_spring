package shopipi.click.models.request;

import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdsReq implements Serializable {
  @NotNull(message = "ids is required")
  private List<String> ids;
}
