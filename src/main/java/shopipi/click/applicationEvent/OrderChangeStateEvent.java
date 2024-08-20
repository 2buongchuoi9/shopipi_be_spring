package shopipi.click.applicationEvent;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import shopipi.click.entity.Notification;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderChangeStateEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;

  private Notification notification;

  // khi trạng thái của đơn hàng thay đổi chỉ thông báo cho khách hàng
  public OrderChangeStateEvent(Object source, Notification notification) {
    super(source);
    this.notification = notification;
  }

}
