package shopipi.click.entity.productSchema;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
public abstract class Attribute {
  @Id
  protected String id;
  protected String productId;
  protected Integer quantity;
  // protected String type;

  // Define a protected constructor to be used by the builder in the subclass
  protected Attribute(String id, String productId, Integer quantity) {
    this.id = id;
    this.productId = productId;
    this.quantity = quantity;
  }

  @Data
  @Builder
  public static class AttributeBuilder {
    protected String id;
    protected String productId;
    protected Integer quantity;
  }

}
