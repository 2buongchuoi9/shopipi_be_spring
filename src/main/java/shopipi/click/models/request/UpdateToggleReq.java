package shopipi.click.models.request;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.internal.util.Lists;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import shopipi.click.utils._enum.ProductState;

@Builder
@Data
public class UpdateToggleReq implements java.io.Serializable {
  @NotNull(message = "ids is required")
  private Set<String> ids;
  @NotNull(message = "isDeleted is required")
  private String value;

  @AssertTrue(message = "value is invalid")
  public boolean isValidateValue() {
    // check value is ProductState
    // Convert ProductState to a List
    List<String> productStates = Arrays.stream(ProductState.values())
        .map(Enum::name)
        .collect(Collectors.toList());

    // Check if value is in the list
    return productStates.contains(value);
  }

}
