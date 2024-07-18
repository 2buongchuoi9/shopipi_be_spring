package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Order;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.OrderParamsReq;
import shopipi.click.models.request.OrderReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.MomoService;
import shopipi.click.services.OrderService;
import shopipi.click.services.VnpayService;
import shopipi.click.utils.Constants.HASROLE;
import shopipi.click.utils._enum.ProductState;
import shopipi.click.utils._enum.StateOrderEnum;
import shopipi.click.utils._enum.TypePayment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/api/order"))
public class OrderController {
  final OrderService orderService;
  final VnpayService vnpayService;
  final HttpServletRequest request;
  final UserRepo userRepo;
  final MomoService momoService;

  @PostMapping("/checkout-review")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Order>> checkoutReview(@AuthenticationPrincipal UserRoot userRoot,
      @RequestBody OrderReq orderReq) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(orderService.checkoutReview(userRoot.getUser(),
            orderReq)));
  }

  @PostMapping("/checkout-review-guest/{userId}")
  public ResponseEntity<MainResponse<Order>> checkoutReviewGuest(@PathVariable String userId,
      @RequestBody OrderReq orderReq) {

    User user = userRepo.findById(userId).orElseThrow(() -> new NotFoundError("User not found"));

    return ResponseEntity.ok()
        .body(MainResponse.oke(orderService.checkoutReview(user,
            orderReq)));
  }

  @PostMapping("/order-by-user")
  public ResponseEntity<MainResponse<?>> orderByUser(@AuthenticationPrincipal UserRoot userRoot,
      @RequestParam(defaultValue = "") String urlRedirect,
      @RequestBody OrderReq orderReq) throws Exception {

    List<Order> orders = orderService.orderByUser(userRoot.getUser(), orderReq);
    if (orderReq.getPayment().equals(TypePayment.CASH.name())) {
      return ResponseEntity.ok().body(MainResponse.oke(orders));
    } else if (orderReq.getPayment().equals(TypePayment.MOMO.name())) {
      Map<String, Object> result = momoService.createPayment(orders, urlRedirect);
      result.put("url", result.get("payUrl"));
      return ResponseEntity.ok().body(MainResponse.oke(result));
    } else {
      Map<String, String> result = new HashMap<>();
      result.put("url", vnpayService.createPaymentUrl(request, orders,
          urlRedirect));
      // result.put("url", vnpayService.createPaymentUrl(request, order,
      // urlRedirect));
      return ResponseEntity.ok().body(MainResponse.oke(result));
    }

  }

  @PostMapping("/update-state-by-shop/{id}")
  public ResponseEntity<MainResponse<?>> updateStateByShop(@PathVariable String id, @RequestParam String state) {

    // check state in StateOrderEnum
    if (Arrays.stream(ProductState.values())
        .map(Enum::name)
        .collect(Collectors.toList()).contains(state))
      throw new NotFoundError("state", state);

    return ResponseEntity.ok().body(MainResponse.oke(orderService.setStateOrderByAdmin(id, state, "c")));
  }

  @GetMapping("")
  // @PreAuthorize(HASROLE.USER)
  public ResponseEntity<MainResponse<PageCustom<Order>>> findOrder(
      @PageableDefault(page = 0, size = 10) Pageable pageable, @ModelAttribute OrderParamsReq params) {

    return ResponseEntity.ok().body(MainResponse.oke(orderService.findOrder(pageable, params)));
  }

}
