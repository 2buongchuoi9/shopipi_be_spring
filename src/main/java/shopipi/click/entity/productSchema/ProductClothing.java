package shopipi.click.entity.productSchema;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "ProductClothings")
@CompoundIndex(name = "index_productId_color_size", def = "{productId: 1, color: 1, size: 1}")
@Data
@EqualsAndHashCode(callSuper = true) // This annotation is used to generate equals and hashcode methods in the class
public class ProductClothing extends Attribute {
  private String size;
  private String color;
  private String material;
  private String brand;

  @Builder
  public ProductClothing(String id, String productId, Integer quantity, String size, String color, String material,
      String brand) {
    super(id, productId, quantity);
    // this.type = "CLOTHING";
    this.size = size;
    this.color = color;
    this.material = material;
    this.brand = brand;
  }

}
