package shopipi.click.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import shopipi.click.applicationEvent.OrderChangeStateEvent;
import shopipi.click.applicationEvent.OrderEvent;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.entity.Cart;
import shopipi.click.entity.Discount;
import shopipi.click.entity.Notification;
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
  private final ApplicationEventPublisher eventPublisher;

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
    Double totalShipping = OrderShipping.valueOf(orderReq.getShippingType()).getCost();
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

  // important event
  // remove cache product
  @CacheEvict(value = "product", allEntries = true)
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

    // important
    eventPublisher.publishEvent(new OrderEvent(this, order));

    // remove product in cart and set quantity in productAttribute
    order.getItems().stream()
        .forEach(shopOrderItem -> {
          shopOrderItem.getItems().stream()
              .forEach(item -> {
                cartService.removeProductItemsToCart(user, item.getVariant().getId());
                // cập nhật số lượng của biến thể sản phẩm
                item.getVariant().setQuantity(item.getVariant().getQuantity() -
                    item.getQuantity());

                // cập nhật số lượng đã bán của biến thể sản phẩm
                item.getVariant().setSold(null == item.getVariant().getSold() ? item.getQuantity()
                    : item.getVariant().getSold() + item.getQuantity());

                // lưu data biến thể sản phẩm
                variationRepo.save(item.getVariant());
                iUpdateProduct.inventory(item.getProduct());
              });
        });

    return order;
  }

  // viết hàm xóa order và cập nhật tất cả:
  // số lượng của biến thể, số lượng đã bán của sản phẩm
  // important. Remove cache product
  @CacheEvict(value = "product", allEntries = true)
  public void removeOrderAndReturnProductQuantityBecausePaymentFail(String orderId) {
    // Tìm đơn hàng theo ID
    Order order = orderRepo.findById(orderId)
        .orElseThrow(() -> new NotFoundError("Order not found"));

    // Duyệt qua từng shop order item trong đơn hàng
    order.getItems().forEach(shopOrderItem -> {
      // Duyệt qua từng product item trong shop order item
      shopOrderItem.getItems().forEach(item -> {
        // Khôi phục lại số lượng của biến thể sản phẩm
        Variant variant = item.getVariant();
        variant.setQuantity(variant.getQuantity() + item.getQuantity());

        // Khôi phục lại số lượng đã bán của biến thể sản phẩm
        if (variant.getSold() != null) {
          variant.setSold(variant.getSold() - item.getQuantity());
        }

        // Lưu lại biến thể sản phẩm
        variationRepo.save(variant);

        // Cập nhật thông tin sản phẩm tổng thể
        iUpdateProduct.inventory(item.getProduct());
      });
    });

    // Xóa đơn hàng khỏi cơ sở dữ liệu
    orderRepo.delete(order);
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

    eventPublisher.publishEvent(new OrderChangeStateEvent(this,
        Notification.builder()
            .userFrom(null)
            .userTo(foundOrder.getUser().getId())
            .content(getContentByChangeState(state))
            .build()));

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

    if (params.getStartDate() != null && params.getEndDate() != null) {
      query.addCriteria(Criteria.where("createdAt").gte(params.getStartDate()).lte(params.getEndDate()));
    } else if (params.getStartDate() != null) {
      query.addCriteria(Criteria.where("createdAt").gte(params.getStartDate()));
    } else if (params.getEndDate() != null) {
      query.addCriteria(Criteria.where("createdAt").lte(params.getEndDate()));
    }

    long total = mongoTemplate.count(query, Order.class);
    List<Order> list = mongoTemplate.find(query, Order.class);

    // list.forEach(v -> {
    // v.getItems().forEach(v1 -> {
    // v1.getItems().forEach(v2 -> {
    // v2.getProduct().getVariants().forEach(
    // variant -> {
    // if (variant.getId().equals(v2.getVariant().getId())) {
    // variant.setSold(null == v2.getVariant().getSold() ? v2.getQuantity()
    // : v2.getVariant().getSold() + v2.getQuantity());
    // variationRepo.save(variant);
    // iUpdateProduct.inventory(v2.getProduct());
    // }
    // });

    // });

    // });
    // });

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

  private String getContentByChangeState(String state) {

    if (state.equalsIgnoreCase(StateOrderEnum.PENDING.name()))
      return "Đơn hàng của bạn đã được đặt";
    if (state.equalsIgnoreCase(StateOrderEnum.CANCELLED.name()))
      return "Đơn hàng của bạn đã bị hủy";
    if (state.equalsIgnoreCase(StateOrderEnum.CONFIRMED.name()))
      return "Đơn hàng của bạn đã được shop xác nhận";
    if (state.equalsIgnoreCase(StateOrderEnum.SHIPPING.name()))
      return "Đơn hàng của bạn đã được giao cho đơn vị vận chuyển";
    if (state.equalsIgnoreCase(StateOrderEnum.DELIVERED.name()))
      return "Đơn hàng của bạn đã được giao thành công";

    return "Đơn hàng của bạn đã được thay đổi trạng thái";
  }

}
