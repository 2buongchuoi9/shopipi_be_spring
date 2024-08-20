package shopipi.click.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "ExpiryDateSale")
@Data
@Builder
public class ExpiryDateSale {
  private String id;

  private LocalDateTime expiryDate;
}
