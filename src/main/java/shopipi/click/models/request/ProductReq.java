package shopipi.click.models.request;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.entity.productSchema.AttributeClothing;
import shopipi.click.entity.productSchema.AttributeElectronic;
import shopipi.click.utils._enum.ProductState;
import shopipi.click.utils._enum.ProductTypeEnum;

@Data
// @AttributeClothing
@AllArgsConstructor
// @NoArgsConstructor
// @AttributeElectronic
public class ProductReq implements Serializable {

  @NotEmpty(message = "Name is required")
  private String name;

  @NotEmpty(message = "slug is required")
  private String slug;

  @NotEmpty(message = "thumb is required")
  private String thumb;

  private String video;

  @Default
  private List<String> images = new ArrayList<String>();

  // @NotNull(message = "price is required")
  // @Min(value = 0, message = "price must be greater than 0")
  // private Double price;

  // @NotNull(message = "priceImport is required")
  // @Min(value = 0, message = "priceImport must be greater than 0")
  // private Double priceImport;

  @NotEmpty(message = "type is required")
  private String type; // ProductTypeEnum.ELECTRONIC.name()

  private String description;

  @NotEmpty(message = "category is required")
  private String categoryId;

  @Default
  private String state = ProductState.HIDDEN.name();
  @Default
  private Boolean isDeleted = false;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = AttributeClothing.class, name = "CLOTHING"),
      @JsonSubTypes.Type(value = AttributeElectronic.class, name = "ELECTRONIC"),
      @JsonSubTypes.Type(value = Attribute.class, name = "OTHER")
  })
  private Attribute attribute;

  private List<Variant> variants;

  @JsonCreator
  public ProductReq(@JsonProperty("attribute") Attribute attribute, @JsonProperty("type") String type) {
    this.attribute = attribute;
    this.type = type;
  }

  @AssertTrue(message = "type not type of ProductTypeEnum")
  private boolean isValidType() {
    return Arrays.asList(ProductTypeEnum.values()).stream()
        .map(v -> v.name())
        .collect(Collectors.toList())
        .contains(this.type);
  }
}
