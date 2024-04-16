package shopipi.click.models.request;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.ProductClothing;
import shopipi.click.entity.productSchema.ProductElectronic;
import shopipi.click.utils._enum.ProductTypeEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttributeReq {

  @NotEmpty(message = "type is required")
  private String type; // ProductTypeEnum.ELECTRONIC.name()

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
  @JsonSubTypes({
      @JsonSubTypes.Type(value = ProductClothing.class, name = "CLOTHING"),
      @JsonSubTypes.Type(value = ProductElectronic.class, name = "ELECTRONIC")
  })
  private List<Attribute> attributes;

  @JsonCreator
  public AttributeReq(@JsonProperty("attributes") List<Attribute> attributes, @JsonProperty("type") String type) {
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
