package shopipi.click.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.entity.Sale;
import shopipi.click.models.paramsRequest.SaleParamsReq;
import shopipi.click.models.request.SaleReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.SaleService;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {
  final SaleService saleService;

  @PostMapping("")
  public ResponseEntity<MainResponse<Sale>> addSale(@RequestBody SaleReq saleReq) {
    return ResponseEntity.ok(MainResponse.oke(saleService.addSale(saleReq)));
  }

  @PostMapping("/{id}")
  public ResponseEntity<MainResponse<Sale>> updateSale(@PathVariable String id, @RequestBody SaleReq saleReq) {
    return ResponseEntity.ok(MainResponse.oke(saleService.updateSale(id, saleReq)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MainResponse<Boolean>> deleteSale(@PathVariable String id) {
    return ResponseEntity.ok(MainResponse.oke(saleService.deleteSale(id)));
  }

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Sale>>> find(@PageableDefault(page = 0, size = 10) Pageable pageable,
      @ModelAttribute SaleParamsReq params) {
    return ResponseEntity.ok(MainResponse.oke(saleService.find(pageable, params)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MainResponse<Sale>> getOne(@PathVariable String id) {
    return ResponseEntity.ok().body(MainResponse.oke(saleService.findById(id)));
  }

}
