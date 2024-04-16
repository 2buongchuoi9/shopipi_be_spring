package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.models.request.AttributeReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.ProductService;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
  final ProductService productService;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("")
  public ResponseEntity<MainResponse<Product>> add(@RequestBody ProductReq productReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.addProduct(productReq)));
  }

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Product>>> findProduct(
      @PageableDefault(page = 0, size = 10) Pageable pageable) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.findProduct(pageable)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/attribute/{productId}")
  public ResponseEntity<MainResponse<Product>> addAttribute(
      @PathVariable String productId,
      @RequestBody AttributeReq attributeReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.addProductAttribute(productId, attributeReq)));
  }

}
