package shopipi.click.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Document(collection = "Brands")
@Data
@Builder
public class Brand {

  @Id
  private String Id;

  @NotEmpty(message = "Brand name is required")
  private String name;

}
