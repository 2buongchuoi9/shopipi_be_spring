package shopipi.click.services.productService;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import shopipi.click.entity.productSchema.Attribute;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.entity.productSchema.AttributeClothing;
import shopipi.click.entity.productSchema.AttributeElectronic;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.models.request.ProductReq;
import shopipi.click.repositories.ProductRepo;
import shopipi.click.utils._enum.ProductTypeEnum;

@Component
@RequiredArgsConstructor
public class ProductFactory {
  // private final ProductRepo productRepo;
  // private final ProductClothingRepo clothingRepo;
  // private final ProductElectronicRepo electronicRepo;

  // public Product createProduct(ProductReq productReq) {
  // // create product
  // Product product = productRepo.save(
  // Product.builder().name(productReq.getName())
  // .thumb(productReq.getThumb())
  // .video(productReq.getVideo())
  // .images(productReq.getImages())
  // .price(productReq.getPrice())
  // .priceImport(productReq.getPriceImport())
  // .type(productReq.getType())
  // .description(productReq.getDescription())
  // .status(productReq.getStatus())
  // .isDeleted(productReq.getIsDeleted())
  // .build());

  // // create Attribute
  // // if (productReq.getAttributes() != null) {
  // // product.setAttributes(createdAttributes(product, productReq.getType(),
  // // productReq.getAttributes()));
  // // }

  // return product;
  // }

  // public List<Attribute> createdAttributes(Product product, String type,
  // List<Attribute> attributes) {
  // ProductTypeEnum productType = ProductTypeEnum.valueOf(type.toUpperCase());
  // switch (productType) {
  // case CLOTHING:
  // // add attribute clothing
  // List<Attribute> clothings = attributes.stream()
  // .map(v -> {
  // AttributeClothing clothing = (AttributeClothing) v;
  // if (clothingRepo.existsByColorAndSizeAndProductId(clothing.getColor(),
  // clothing.getSize(),
  // product.getId()))
  // throw new DuplicateRecordError("Color and size already exists");
  // clothing.setProductId(product.getId());
  // return clothingRepo.save(clothing);
  // }).collect(Collectors.toList());
  // // update inventory (quantity in product)
  // product.setQuantity(clothings.stream().mapToInt(v -> v.getQuantity()).sum());
  // return clothings;
  // case ELECTRONIC:
  // // add attribute electronic
  // List<Attribute> electronics = attributes.stream()
  // .map(v -> {
  // AttributeElectronic electronic = (AttributeElectronic) v;
  // if (electronicRepo.existsByColorAndModelAndProductId(electronic.getColor(),
  // electronic.getModel(),
  // product.getId()))
  // throw new DuplicateRecordError("Color and model already exists");
  // electronic.setProductId(product.getId());
  // return electronicRepo.save(electronic);
  // }).collect(Collectors.toList());
  // // update inventory (quantity in product)
  // product.setQuantity(electronics.stream().mapToInt(v ->
  // v.getQuantity()).sum());
  // return electronics;
  // default:
  // throw new BabRequestError("Product is already but can't add attribute
  // because: " + type + " not found");
  // }
  // }

}
