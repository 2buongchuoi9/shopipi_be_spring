package shopipi.click.services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Cart;
import shopipi.click.entity.Discount;
import shopipi.click.entity.Order;
import shopipi.click.entity.User;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.models.ShopOrderItemsModel.ProductItemsModel;
import shopipi.click.models.request.OrderReq;
import shopipi.click.models.request.OrderReq.ShopOrderItemsReq;
import shopipi.click.repositories.CartRepo;
import shopipi.click.repositories.DiscountRepo;
import shopipi.click.repositories.OrderRepo;
import shopipi.click.services.productService.IUpdateProduct;
import shopipi.click.utils._enum.StateOrderEnum;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepo orderRepo;
  private final CartService cartService;
  private final CartRepo cartRepo;
  private final DiscountRepo discountRepo;
  private final DiscountService discountService;
  private final IUpdateProduct iUpdateProduct;

  public Order checkoutReview(User user, OrderReq orderReq) {

    AtomicReference<Double> totalCapital = new AtomicReference<>(0.0);
    AtomicReference<Double> totalDiscount = new AtomicReference<>(0.0);
    AtomicReference<Double> total = new AtomicReference<>(0.0);

    List<ShopOrderItemsModel> shopOrderItems = orderReq.getShopOrderItems().stream()
        .map(shopOrderItemReq -> {
          List<ProductItemsModel> items = shopOrderItemReq.getItems().stream()
              .map(item -> cartService.checkoutProductServer(item)).collect(Collectors.toList());

          return ShopOrderItemsModel.builder()
              .shopId(shopOrderItemReq.getShopId())
              .discountId(shopOrderItemReq.getDiscountId())
              .items(items)
              .build();
        })
        .map(shopOrderItem -> {
          // calculate discount amount in 1 ShopOrderItemsModel
          if (shopOrderItem.getDiscountId() != null) {
            Discount discount = discountRepo.findById(shopOrderItem.getDiscountId())
                .orElseThrow(() -> new NotFoundError("Discount not found"));
            Double[] totals = discountService.getDiscountAmount(shopOrderItem, discount, user.getId());
            shopOrderItem.setTotalDiscount(totals[0]);
            shopOrderItem.setTotal(totals[1]);
          } else {
            shopOrderItem.setTotalDiscount(0.0);
            // calculate total in 1 ShopOrderItemsModel
            shopOrderItem.setTotal(shopOrderItem.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum());
          }
          shopOrderItem.getItems().forEach(item -> {
            totalCapital.updateAndGet(v -> v + (item.getProduct().getPriceImport() * item.getQuantity()));
          });
          totalDiscount.updateAndGet(v -> v + shopOrderItem.getTotalDiscount());
          total.updateAndGet(v -> v + shopOrderItem.getTotal());
          return shopOrderItem;
        })
        .collect(Collectors.toList());

    Double totalShipping = orderReq.getAddress().length() <= 2 ? 0.0 : orderReq.getAddress().length() * 1000.0;
    Double revenue = total.get() - totalDiscount.get() + totalShipping;
    // calculate total capital

    return Order.builder()
        .items(shopOrderItems)
        .totalDiscount(totalDiscount.get())
        .totalShipping(totalShipping)
        .totalCheckout(revenue)
        .capital(totalCapital.get())
        .revenue(revenue)
        .profit(revenue - totalCapital.get())
        .build();
  }

  public Order orderByUser(User user, OrderReq orderReq) {
    Order order = checkoutReview(user, orderReq);
    orderRepo.save(Order.builder()
        .items(order.getItems())
        .totalDiscount(order.getTotalDiscount())
        .totalShipping(order.getTotalShipping())
        .totalCheckout(order.getTotalCheckout())
        .capital(order.getCapital())
        .revenue(order.getRevenue())
        .profit(order.getProfit())
        .build());

    // remove product in cart and set quantity in productAttribute
    order.getItems().stream()
        .forEach(shopOrderItem -> {
          shopOrderItem.getItems().stream()
              .forEach(item -> {
                cartService.removeProductItemsToCart(user, item.getAttribute().getId());
                item.getAttribute().setQuantity(item.getAttribute().getQuantity() - item.getQuantity());
                iUpdateProduct.inventory(item.getProduct());
              });
        });

    return order;
  }

  public Order findOrderById(String orderId) {
    Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundError("orderId", orderId));
    return order;
  }

  public Order setStateOrderByAdmin(String orderId, String state, String note) {
    Order foundOrder = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundError("orderId", orderId));
    foundOrder.setState(state);
    foundOrder.setNote(foundOrder.getNote() + "\n" + note + "\t"
        + LocalDate.now().format(DateTimeFormatter.ofPattern(WebMvcConfig.dateTimeFormat)));
    return orderRepo.save(foundOrder);
  }

  public Order cancelOrderByUser(String orderId, String note) {
    Order foundOrder = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundError("orderId", orderId));
    if (!foundOrder.getState().equalsIgnoreCase(StateOrderEnum.PENDING.name()))
      throw new BabRequestError("Order is not pending, can't cancel it");
    return setStateOrderByAdmin(orderId, StateOrderEnum.CANCELLED.name(), note);
  }

  public List<Order> findOrdersByUser(User user, String state) {
    return state.isEmpty() || state == null ? orderRepo.findByUserId(user.getId())
        : orderRepo.findByUserIdAndState(user.getId(), state);
  }
}
