package shopipi.click.entity.productSchema;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true) // This annotation is used to generate
// equals and hashcode methods in the class
public class AttributeClothing extends Attribute implements Serializable {
  private String material; // chat lieu
  private String model; // chat lieu
  private String season; // mùa (đông, hè, xuân, thu)
  private String style; // phong cách (thể thao, dạo phố, công sở, dự tiệc)

  public AttributeClothing(String brand, String origin, String material, String model,
      String season, String style) {
    super(brand, origin);
    this.material = material;
    this.model = model;
    this.season = season;
    this.style = style;
  }

}
