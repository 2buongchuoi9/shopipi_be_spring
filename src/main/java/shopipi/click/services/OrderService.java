package shopipi.click.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Cart;
import shopipi.click.entity.Discount;
import shopipi.click.entity.Order;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Variant;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.models.ShopOrderItemsModel.ProductItemsModel;
import shopipi.click.models.paramsRequest.OrderParamsReq;
import shopipi.click.models.request.OrderReq;
import shopipi.click.models.request.OrderReq.ShopOrderItemsReq;
import shopipi.click.repositories.CartRepo;
import shopipi.click.repositories.DiscountRepo;
import shopipi.click.repositories.OrderRepo;
import shopipi.click.repositories.VariantRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.productService.IUpdateProduct;
import shopipi.click.utils._enum.OrderShipping;
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
  private final VariantRepo variationRepo;
  private final MongoTemplate mongoTemplate;

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
            Double[] totals = discountService.getDiscountAmount(shopOrderItem, discount,
                user.getId());
            shopOrderItem.setTotalDiscount(totals[0]);
            shopOrderItem.setTotal(totals[1]);
          } else {
            shopOrderItem.setTotalDiscount(0.0);
            // calculate total in 1 ShopOrderItemsModel
            shopOrderItem.setTotal(shopOrderItem.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum());
          }
          shopOrderItem.getItems().forEach(item -> {
            totalCapital.updateAndGet(v -> v + (item.getVariant().getPriceImport() *
                item.getQuantity()));
          });
          totalDiscount.updateAndGet(v -> v + shopOrderItem.getTotalDiscount());
          total.updateAndGet(v -> v + shopOrderItem.getTotal());
          return shopOrderItem;
        })
        .collect(Collectors.toList());

    // Double totalShipping = orderReq.getAddress().length() <= 2 ? 0.0 :
    // orderReq.getAddress().length() * 1000.0;
    Double totalShipping = OrderShipping.valueOf(orderReq.getShippingType()).getCost() * 1000;
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
        .totalOrder(total.get())
        .build();
  }

  public Order orderByUser_OneShop(User user, OrderReq orderReq) {
    Order order = checkoutReview(user, orderReq);

    order = orderRepo.save(Order.builder()
        .items(order.getItems())
        .totalDiscount(order.getTotalDiscount())
        .totalShipping(order.getTotalShipping())
        .totalCheckout(order.getTotalCheckout())
        .capital(order.getCapital())
        .revenue(order.getRevenue())
        .profit(order.getProfit())
        .totalOrder(order.getTotalCheckout() + order.getTotalShipping() + order.getTotalDiscount())
        .payment(orderReq.getPayment())
        .notes(Arrays.asList(
            "Tạo hóa đơn date:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(WebMvcConfig.dateTimeFormat))
                + "content: " + orderReq.getNote()))
        .build());

    // remove product in cart and set quantity in productAttribute
    order.getItems().stream()
        .forEach(shopOrderItem -> {
          shopOrderItem.getItems().stream()
              .forEach(item -> {
                cartService.removeProductItemsToCart(user, item.getVariant().getId());
                item.getVariant().setQuantity(item.getVariant().getQuantity() -
                    item.getQuantity());
                iUpdateProduct.inventory(item.getProduct());
              });
        });

    return order;
  }

  public List<Order> orderByUser(User user, OrderReq orderReq) {

    List<OrderReq> listOrderRequests = orderReq.getShopOrderItems().stream()
        .map(shopOrderItemReq -> OrderReq.builder()
            .address(orderReq.getAddress())
            .shippingType(orderReq.getShippingType())
            .payment(orderReq.getPayment())
            .shopOrderItems(List.of(shopOrderItemReq))
            .build())
        .collect(Collectors.toList());

    List<Order> listOrders = listOrderRequests.stream()
        .map(orderRequest -> orderByUser_OneShop(user, orderRequest))
        .collect(Collectors.toList());

    return listOrders;
  }

  public Order findOrderById(String orderId) {
    Order order = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundError("orderId", orderId));
    return order;
  }

  public Order setStateOrderByAdmin(String orderId, String state, String note) {
    Order foundOrder = orderRepo.findById(orderId).orElseThrow(() -> new NotFoundError("orderId", orderId));
    foundOrder.setState(state);
    foundOrder.addNotes("Change state by shop date:"
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern(WebMvcConfig.dateTimeFormat))
        + "content: " + note);
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

  public PageCustom<Order> findOrder(Pageable pageable, OrderParamsReq params) {
    Query query = new Query();

    if (params.getState() != null && !params.getState().isEmpty())
      query.addCriteria(Criteria.where("state").is(params.getState()));

    if (params.getShopId() != null && !params.getShopId().isEmpty())
      query.addCriteria(Criteria.where("items.0.shopId").is(params.getShopId()));

    if (params.getUserId() != null && !params.getUserId().isEmpty())
      query.addCriteria(Criteria.where("user.id").is(params.getUserId()));

    long total = mongoTemplate.count(query, Order.class);
    List<Order> list = mongoTemplate.find(query, Order.class);

    return new PageCustom<Order>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }

  // public void removeOrderAndReturnProductQuantityBecausePaymentFail(String
  // orderId) {
  // Order order = orderRepo.findById(orderId).orElse(null);
  // if (order != null) {
  // // return quantity product
  // order.getItems().stream().map(v -> {
  // Variant variation = variationRepo.findById(v.getItems()).get();
  // variation.setQuantity(variation.getQuantity() + v.getQuantity());
  // variationRepo.save(variation);
  // return v;
  // });
  // // remove order
  // orderRepo.delete(order);
  // }
  // }

}
