package shopipi.click.applicationEvent;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import shopipi.click.entity.Notification;
import shopipi.click.entity.Order;

@Data
@EqualsAndHashCode(callSuper = false)
public class OrderEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;

  private Order order;

  // nhận vào 1 list các thông báo
  // thông báo cho khách hàng và cho shop
  public OrderEvent(Object source, Order order) {
    super(source);
    this.order = order;
  }

}
