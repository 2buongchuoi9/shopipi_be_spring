package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.api.exceptions.NotFound;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Cart;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.request.AddDiscountToCartReq;
import shopipi.click.models.request.CartReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.UserRepo;
import shopipi.click.services.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
  final CartService cartService;
  final UserRepo userRepo;

  @PostMapping("/add-to-cart")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> addCart(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid CartReq cartReq) {

    return ResponseEntity.ok().body(MainResponse.oke(cartService.addToCart(userRoot.getUser(),
        cartReq)));
  }

  @PostMapping("/add-to-cart/by-user-mod/{id}")
  public ResponseEntity<MainResponse<Cart>> addCartByUserMod(@PathVariable String id,
      @RequestBody @Valid CartReq cartReq) {

    User user = userRepo.findById(id).orElseThrow(() -> new NotFoundError("User not found"));

    return ResponseEntity.ok().body(MainResponse.oke(cartService.addToCart(user,
        cartReq)));
  }

  @PostMapping("/add-discount-to-cart")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> addDiscountToCart(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid AddDiscountToCartReq addDiscountToCartReq) {

    return ResponseEntity.ok().body(MainResponse.oke(cartService.addDiscountToShop(userRoot.getUser(),
        addDiscountToCartReq.getShopId(), addDiscountToCartReq.getDiscountId())));
  }

  @Operation(summary = "Get cart by user")
  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Cart>> getCart(@AuthenticationPrincipal UserRoot userRoot) {
    return ResponseEntity.ok().body(MainResponse.oke(cartService.getCart(userRoot.getUser())));
  }

  @Operation(summary = "Get cart by user")
  @GetMapping("/{id}")
  public ResponseEntity<MainResponse<Cart>> getCartByUserMod(@PathVariable String id) {

    User user = userRepo.findById(id).orElseThrow(() -> new NotFoundError("User not found"));

    return ResponseEntity.ok().body(MainResponse.oke(cartService.getCart(user)));
  }

}
