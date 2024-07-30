package shopipi.click.applicationEvent;

import org.springframework.context.ApplicationEvent;

import shopipi.click.entity.Notification;
import shopipi.click.utils._enum.NotificationType;

public class LikeEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;

  private Notification notification;

  public LikeEvent(Object source, Notification notification) {
    super(source);
    this.notification = notification;
    this.notification.setNotificationType(NotificationType.LIKE);
  }

}
