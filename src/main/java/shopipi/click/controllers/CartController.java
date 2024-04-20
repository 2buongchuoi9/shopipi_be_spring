package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Cart;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.request.AddDiscountToCartReq;
import shopipi.click.models.request.CartReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
  final CartService cartService;

  @PostMapping("/add-to-cart")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> postMethodName(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid CartReq cartReq) {

    return ResponseEntity.ok().body(MainResponse.oke(cartService.addToCart(userRoot.getUser(), cartReq)));
  }

  @PostMapping("/add-discount-to-cart")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> addDiscountToCart(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid AddDiscountToCartReq addDiscountToCartReq) {

    return ResponseEntity.ok().body(MainResponse.oke(cartService.addDiscountToShop(userRoot.getUser(),
        addDiscountToCartReq.getShopId(), addDiscountToCartReq.getDiscountId())));
  }

  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> getCart(@AuthenticationPrincipal UserRoot userRoot) {
    return ResponseEntity.ok().body(MainResponse.oke(cartService.getCart(userRoot.getUser())));
  }

}
