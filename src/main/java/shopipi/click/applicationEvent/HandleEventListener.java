package shopipi.click.applicationEvent;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shopipi.click.services.NotifyService;

@Component
@RequiredArgsConstructor
public class HandleEventListener {
  private final NotifyService notifyService;

  private final SimpMessagingTemplate messagingTemplate;

  @EventListener
  @Async
  public void handleFollowEvent(FollowEvent followEvent) {

    var notification = followEvent.getNotification();
    notifyService.createNotification(followEvent.getNotification());
    messagingTemplate.convertAndSendToUser(notification.getUserTo(), "/notifications", notification);
  }
}
