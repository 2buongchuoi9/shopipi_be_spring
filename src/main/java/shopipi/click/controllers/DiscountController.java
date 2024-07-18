package shopipi.click.controllers;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Discount;
import shopipi.click.models.paramsRequest.DiscountParamsReq;
import shopipi.click.models.request.DiscountReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.DiscountService;
import shopipi.click.utils.Constants.HASROLE;
import org.springframework.web.bind.annotation.PutMapping;

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

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Discount>>> getAll(
      @PageableDefault(size = 10, page = 0) Pageable pageable, @ModelAttribute DiscountParamsReq discountParams) {
    return ResponseEntity.ok().body(MainResponse.oke(discountService.finDiscounts(pageable, discountParams)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<MainResponse<Discount>> getOne(@PathVariable String id) {
    return ResponseEntity.ok().body(MainResponse.oke(discountService.findById(id)));
  }

  @PostMapping("/{id}")
  @PreAuthorize(HASROLE.SHOP)
  public ResponseEntity<MainResponse<Discount>> updateDiscount(@PathVariable String id,
      @RequestBody @Valid DiscountReq discountReq) {
    return ResponseEntity.ok().body(MainResponse.oke(discountService.updateDiscount(id, discountReq)));
  }

}
