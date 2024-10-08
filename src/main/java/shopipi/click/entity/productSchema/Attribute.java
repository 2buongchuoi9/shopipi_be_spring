package shopipi.click.entity.productSchema;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class Attribute implements Serializable {
  // @Id
  // private String id;
  // private String productId;
  // private Integer quantity;
  // private String type;
  protected String brand; // thuong hieu
  protected String origin; // xuat xu
  private List<ObjectMap> listAttribute; // những thông số khác của sản phẩm
  private List<ListObjectMap> listVariant; // [{key: "size", value: ["M", "L", "XL"]}, {key: "color", value: ["red",
  // "blue"]}]

  // Define a private constructor to be used by the builder in the subclass
  public Attribute(String brand, String origin) {
    this.brand = brand;
    this.origin = origin;
  }

  @Data
  @Builder
  public static class AttributeBuilder implements Serializable {
    private String brand; // thuong hieu
    private String origin; // xuat xu
    private ListObjectMap listVariant;

  }

  @Data
  @Builder
  public static class ObjectMap implements Serializable {
    private String key;
    private String value;
  }

  @Data
  @Builder
  public static class ListObjectMap implements Serializable {
    private String key;
    private List<String> values;
  }

}
