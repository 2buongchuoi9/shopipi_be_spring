package shopipi.click.applicationEvent;

import org.springframework.context.ApplicationEvent;

import lombok.Data;
import lombok.EqualsAndHashCode;
import shopipi.click.entity.Rating;

@Data
@EqualsAndHashCode(callSuper = false)
public class RatingEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;

  private Rating rating;

  public RatingEvent(Object source, Rating rating) {
    super(source);
    this.rating = rating;
  }

}
