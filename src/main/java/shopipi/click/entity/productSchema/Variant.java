package shopipi.click.entity.productSchema;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.entity.productSchema.Attribute.ObjectMap;

@Data
@Builder
@Document(collection = "Variants")
public class Variant implements Serializable {
  @Id
  private String id;
  private String productId;
  private Integer quantity;
  @Default
  private Double price = 0.0; // giá gốc
  @Default
  private Double priceSale = 0.0; // giá bán sau khi giảm giá
  @Default
  private Double priceImport = 0.0;
  private List<ObjectMap> valueVariant; // [{key: "size", value: "M"}, {key: "color", value: "red"}]

  @Default
  private Integer sold = 0;
}
