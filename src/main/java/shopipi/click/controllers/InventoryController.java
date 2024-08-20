package shopipi.click.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Inventory;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.models.paramsRequest.InventoryParamsReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.InventoryService;
import shopipi.click.utils.Constants.HASROLE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
  final InventoryService inventoryService;

  @Operation(summary = "nhập hàng")
  @PreAuthorize(HASROLE.SHOP)
  @PostMapping("")
  public ResponseEntity<MainResponse<Product>> addInventory(@RequestBody Inventory inventory) {
    return ResponseEntity.ok().body(MainResponse.oke(inventoryService.addInventory(inventory)));
  }

  @Operation(summary = "nhập hàng nhiều sản phẩm")
  @PreAuthorize(HASROLE.SHOP)
  @PostMapping("/add-many")
  public ResponseEntity<MainResponse<List<Product>>> addManyInventory(@RequestBody List<Inventory> inventories) {
    return ResponseEntity.ok().body(MainResponse.oke(inventoryService.addManyInventory(inventories)));
  }

  @Operation(summary = "lấy lich sử nhập hàng")
  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Inventory>>> findInventory(
      @PageableDefault(size = 10) Pageable pageable, @ModelAttribute InventoryParamsReq params) {
    return ResponseEntity.ok().body(MainResponse.oke(inventoryService.findInventory(pageable, params)));
  }

}
