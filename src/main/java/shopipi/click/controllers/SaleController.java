package shopipi.click.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Sale;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.SaleService;

@RestController
@RequestMapping("/api/sale")
@RequiredArgsConstructor
public class SaleController {
  final SaleService saleService;

  @PostMapping("")
  public ResponseEntity<MainResponse<Sale>> addSale(@RequestBody Sale sale) {
    return ResponseEntity.ok(MainResponse.oke(saleService.addSale(sale)));
  }

  @PostMapping("/{id}")
  public ResponseEntity<MainResponse<Sale>> updateSale(@PathVariable String id, @RequestBody Sale sale) {
    return ResponseEntity.ok(MainResponse.oke(saleService.updateSale(id, sale)));
  }

}
