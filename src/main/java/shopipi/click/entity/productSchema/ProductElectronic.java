package shopipi.click.entity.productSchema;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "ProductElectronics")
@CompoundIndex(name = "index_productId_model", def = "{productId: 1, model: 1}")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductElectronic extends Attribute {
  private String manufacturer; // nha may san xuat
  private String model;
  private String color;
  private String brand;

  @Builder
  public ProductElectronic(String id, String productId, Integer quantity, String manufacturer, String model,
      String color, String brand) {
    super(id, productId, quantity);
    // this.type = "ELECTRONIC";
    this.manufacturer = manufacturer;
    this.model = model;
    this.color = color;
    this.brand = brand;
  }
}
