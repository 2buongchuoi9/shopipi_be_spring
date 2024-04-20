package shopipi.click.models.request;

import java.util.List;

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
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.ProductClothing;
import shopipi.click.entity.productSchema.ProductElectronic;
import shopipi.click.utils._enum.ProductTypeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReq {

  @NotEmpty(message = "Name is required")
  private String name;

  @NotEmpty(message = "thumb is required")
  private String thumb;

  @Default
  private List<String> images = new ArrayList<String>();

  @NotEmpty(message = "price is required")
  @Min(value = 0, message = "price must be greater than 0")
  private Double price;

  @NotEmpty(message = "priceImport is required")
  @Min(value = 0, message = "priceImport must be greater than 0")
  private Double priceImport;

  @NotEmpty(message = "type is required")
  private String type; // ProductTypeEnum.ELECTRONIC.name()

  private String description;

  @NotEmpty(message = "category is required")
  private String categoryId;

  @Default
  private Boolean status = true;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = ProductClothing.class, name = "CLOTHING"),
      @JsonSubTypes.Type(value = ProductElectronic.class, name = "ELECTRONIC")
  })
  private List<Attribute> attributes;

  @JsonCreator
  public ProductReq(@JsonProperty("attributes") List<Attribute> attributes, @JsonProperty("type") String type) {
    this.attributes = attributes;
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
