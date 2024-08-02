package shopipi.click.applicationEvent;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import shopipi.click.entity.Notification;
import shopipi.click.utils._enum.NotificationType;

@Data
@EqualsAndHashCode(callSuper = false)
public class FollowEvent extends ApplicationEvent {
  private Notification notification;

  public FollowEvent(Object source, Notification notification) {
    super(source);
    this.notification = notification;
    this.notification.setNotificationType(NotificationType.NEW_FOLLOW);
  }

  private static final long serialVersionUID = 1L;

}
