package shopipi.click.entity.productSchema;

import java.util.List;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
public class Attribute {
  // @Id
  // private String id;
  // private String productId;
  // private Integer quantity;
  // private String type;
  protected String brand; // thuong hieu
  protected String origin; // xuat xu
  private List<ListObjectMap> listVariant; // [{key: "size", value: ["M", "L", "XL"]}, {key: "color", value: ["red",
  // "blue"]}]

  // Define a private constructor to be used by the builder in the subclass
  public Attribute(String brand, String origin) {
    this.brand = brand;
    this.origin = origin;
  }

  @Data
  @Builder
  public static class AttributeBuilder {
    private String brand; // thuong hieu
    private String origin; // xuat xu
    private ListObjectMap listVariant;

  }

  @Data
  @Builder
  public static class ObjectMap {
    private String key;
    private String value;
  }

  @Data
  @Builder
  public static class ListObjectMap {
    private String key;
    private List<String> values;
  }

}
