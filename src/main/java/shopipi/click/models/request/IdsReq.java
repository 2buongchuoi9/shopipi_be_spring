package shopipi.click.models.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdsReq {
  @NotNull(message = "ids is required")
  private List<String> ids;
}
