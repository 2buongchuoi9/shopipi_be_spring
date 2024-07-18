package shopipi.click.entity.productSchema;

import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AttributeElectronic extends Attribute {
  private String manufacturer; // nha may san xuat
  private String model;
  private String type; // loai san pham (laptop, smartphone, tablet, smartwatch, ...)

  public AttributeElectronic(String brand, String origin, String manufacturer, String model, String brand2,
      String type) {
    super(brand, origin);
    this.manufacturer = manufacturer;
    this.model = model;
    brand = brand2;
    this.type = type;
  }

}
