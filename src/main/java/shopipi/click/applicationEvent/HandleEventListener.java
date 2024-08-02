package shopipi.click.applicationEvent;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Notification;
import shopipi.click.entity.User;
import shopipi.click.repositories.RatingRepo;
import shopipi.click.repositories.UserRepo;
import shopipi.click.services.NotifyService;
import shopipi.click.utils._enum.NotificationType;

@Component
@RequiredArgsConstructor
public class HandleEventListener {
  private final NotifyService notifyService;
  private final UserRepo userRepo;
  private final RatingRepo ratingRepo;
  private final SimpMessagingTemplate messagingTemplate;

  @EventListener
  @Async
  public void handleFollowEvent(FollowEvent followEvent) {
    var notification = followEvent.getNotification();
    notifyService.createNotification(followEvent.getNotification());
    messagingTemplate.convertAndSendToUser(notification.getUserTo(), "/notifications", notification);
  }

  @EventListener
  @Async
  public void handleLikeEvent(LikeEvent likeEvent) {
    var notification = likeEvent.getNotification();
    notifyService.createNotification(likeEvent.getNotification());
    messagingTemplate.convertAndSendToUser(notification.getUserTo(), "/notifications", notification);
  }

  @EventListener
  @Async
  // khi order cần thông báo cho người dùng, cho shop
  public void handleOrderEvent(OrderEvent orderEvent) {
    // lấy thông tin của shop
    User shop = userRepo.findById(orderEvent.getOrder().getItems().get(0).getShopId()).orElse(null);

    if (shop == null)
      return;

    // tạo thông báo cho shop

    var notifyShop = Notification.builder()
        .userFrom(orderEvent.getOrder().getUser())
        .userTo(shop.getId())
        .content("Đơn hàng mới từ " + orderEvent.getOrder().getUser().getName() + " đã được đặt. Tổng tiền: "
            + orderEvent.getOrder().getTotalCheckout())
        .description(orderEvent.getOrder().getId())
        .build();

    // tạo thông báo cho khách hàng
    var notifyUser = Notification.builder()
        .userFrom(shop)
        .userTo(orderEvent.getOrder().getUser().getId())
        .content("Đơn hàng của bạn đã được đặt. Tổng tiền: " + orderEvent.getOrder().getTotalCheckout())
        .description(orderEvent.getOrder().getId())
        .build();

    notifyService.createNotification(notifyShop);
    notifyService.createNotification(notifyUser);

    // gửi thông báo cho shop
    messagingTemplate.convertAndSendToUser(notifyShop.getUserTo(), "/notifications", notifyShop);
    // gửi thông báo cho khách hàng
    messagingTemplate.convertAndSendToUser(notifyUser.getUserTo(), "/notifications", notifyUser);

  }

  @EventListener
  @Async
  // khi trạng thái của đơn hàng thay đổi chỉ thông báo cho khách hàng
  public void handleOrderChangeStateEvent(OrderChangeStateEvent orderChangeStateEvent) {
    var notification = orderChangeStateEvent.getNotification();
    notifyService.createNotification(orderChangeStateEvent.getNotification());
    messagingTemplate.convertAndSendToUser(notification.getUserTo(), "/notifications", notification);
  }

  @EventListener
  @Async
  // thông báo khi có rating hoặc comment mới
  // thông báo cho shop nếu đó là rating hoặc comment trực tiếp
  // thông báo cho người dùng nếu đó là reply
  public void handleRatingEvent(RatingEvent ratingEvent) {
    // kiểm tra có phải reply không
    if (ratingEvent.getRating().getParentId() == null) {
      // thông báo cho shop
      var notifyShop = Notification.builder()
          .userFrom(ratingEvent.getRating().getUser())
          .userTo(ratingEvent.getRating().getShopId())
          .content("Có " + (ratingEvent.getRating().getIsComment() ? "comment" : "rating") + " mới từ "
              + ratingEvent.getRating().getUser().getName())
          .notificationType(NotificationType.NEW_COMMENT)
          .description(ratingEvent.getRating().getId())
          .build();
      notifyService.createNotification(notifyShop);
      messagingTemplate.convertAndSendToUser(notifyShop.getUserTo(), "/notifications", notifyShop);
    } else {
      // lấy thông tin của rating cha
      var parentRating = ratingRepo.findById(ratingEvent.getRating().getParentId()).orElse(null);
      if (parentRating == null)
        return;

      // thông báo cho người dùng
      var notifyUser = Notification.builder()
          .userFrom(ratingEvent.getRating().getUser())
          .userTo(parentRating.getUser().getId())
          .content("Có reply mới từ " + ratingEvent.getRating().getUser().getName())
          .notificationType(NotificationType.NEW_REPLY)
          .description(ratingEvent.getRating().getId())
          .build();

      notifyService.createNotification(notifyUser);
      messagingTemplate.convertAndSendToUser(notifyUser.getUserTo(), "/notifications", notifyUser);
    }
  }

}
