package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.UserRoot;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.models.paramsRequest.ProductParamsReq;
import shopipi.click.models.request.AttributeReq;
import shopipi.click.models.request.ProductReq;
import shopipi.click.models.request.UpdateToggleReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.ProductService;
import shopipi.click.utils.Constants.HASROLE;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
  final ProductService productService;

  @PreAuthorize(HASROLE.SHOP + " or " + HASROLE.ADMIN)
  @PostMapping("")
  public ResponseEntity<MainResponse<Product>> add(@RequestBody @Valid ProductReq productReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.addProduct(productReq)));
  }

  @PreAuthorize(HASROLE.SHOP + " or " + HASROLE.ADMIN)
  @PostMapping("/{id}")
  public ResponseEntity<MainResponse<Product>> update(@PathVariable String id,
      @RequestBody @Valid ProductReq productReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.updateProduct(id, productReq)));
  }

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Product>>> findProduct(
      @PageableDefault(page = 0, size = 10) Pageable pageable, @ModelAttribute ProductParamsReq productParamsReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.findProduct(pageable, productParamsReq)));
  }

  @PostMapping("/find-by-shop")
  @PreAuthorize(HASROLE.SHOP + " or " + HASROLE.ADMIN)
  public ResponseEntity<MainResponse<PageCustom<Product>>> findProductByShop(
      @PageableDefault(page = 0, size = 10) Pageable pageable, @AuthenticationPrincipal UserRoot userRoot,
      @ModelAttribute ProductParamsReq productParamsReq) {

    productParamsReq.setShopId(userRoot.getUser().getId());
    return ResponseEntity.ok().body(
        MainResponse.oke(productService.findProduct(pageable, productParamsReq)));
  }

  @GetMapping("/slug/{slug}")
  public ResponseEntity<MainResponse<Product>> findBySlug(@PathVariable String slug) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.findBySlug(slug)));
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<MainResponse<Product>> findById(@PathVariable String id) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.findById(id)));
  }

  @PostMapping("/update-many-state")
  public ResponseEntity<MainResponse<Boolean>> updateManyState(@RequestBody @Valid UpdateToggleReq updateToggleReq) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.updateManyState(updateToggleReq)));
  }

  // @PreAuthorize("isAuthenticated()")
  // @PostMapping("/attribute/{productId}")
  // public ResponseEntity<MainResponse<Product>> addAttribute(
  // @PathVariable String productId,
  // @RequestBody @Valid AttributeReq attributeReq) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(productService.addProductAttribute(productId,
  // attributeReq)));
  // }

  @DeleteMapping("/{id}")
  public ResponseEntity<MainResponse<Boolean>> delete(@PathVariable String id) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.deleteProduct(id)));
  }

  @GetMapping("/count/{shopId}")
  public ResponseEntity<MainResponse<List<Long>>> countProduct(@PathVariable String shopId) {
    return ResponseEntity.ok().body(MainResponse.oke(productService.countProduct(shopId)));
  }

}
