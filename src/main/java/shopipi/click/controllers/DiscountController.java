package shopipi.click.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.models.request.DiscountReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.DiscountService;
import shopipi.click.utils.Constants.HASROLE;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class DiscountController {
  final DiscountService discountService;

  @PostMapping("")
  @PreAuthorize(HASROLE.SHOP)
  public ResponseEntity<MainResponse<Discount>> addDiscount(@RequestBody @Valid DiscountReq discountReq) {
    return ResponseEntity.ok().body(MainResponse.oke(discountService.addDiscount(discountReq)));
  }

}
