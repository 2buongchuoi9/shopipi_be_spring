package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Order;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.request.OrderReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.OrderService;
import shopipi.click.services.VnpayService;
import shopipi.click.utils.Constants.HASROLE;
import shopipi.click.utils._enum.TypePayment;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/api/order"))
@PreAuthorize("isAuthenticated()")
public class OrderController {
  final OrderService orderService;
  final VnpayService vnpayService;
  final HttpServletRequest request;

  @PostMapping("/checkout-review")
  public ResponseEntity<MainResponse<Order>> checkoutReview(@AuthenticationPrincipal UserRoot userRoot,
      @RequestBody OrderReq orderReq) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(orderService.checkoutReview(userRoot.getUser(), orderReq)));
  }

  @PostMapping("/order-by-user")
  public ResponseEntity<MainResponse<?>> orderByUser(@AuthenticationPrincipal UserRoot userRoot,
      @RequestParam(defaultValue = "") String urlRedirect,
      @RequestBody OrderReq orderReq) throws JsonProcessingException, UnsupportedEncodingException {

    Order order = orderService.orderByUser(userRoot.getUser(), orderReq);
    if (orderReq.getPayment().equals(TypePayment.CASH.name())) {
      return ResponseEntity.ok().body(MainResponse.oke(order));
    } else {
      Map<String, String> result = new HashMap<>();
      result.put("url", vnpayService.createPaymentUrl(request, order, urlRedirect));
      // result.put("url", vnpayService.createPaymentUrl(request, order,
      // urlRedirect));
      return ResponseEntity.ok().body(MainResponse.oke(result));
    }

  }

  // @PostMapping("/find-by-admin")
  // public ResponseEntity<MainResponse<?>> vnpayReturn() {
  // return ResponseEntity.ok().body(MainResponse.oke(orderService.fin);
  // }

  // @PostMapping("/find-by-shop")
  // @PreAuthorize(HASROLE.USER)
  // public ResponseEntity<MainResponse<PageCustom<Order>>>
  // vnpayReturn(@AuthenticationPrincipal UserRoot userRoot) {

  // return
  // ResponseEntity.ok().body(MainResponse.oke(orderService.findOrderById(null)));
  // }

}
