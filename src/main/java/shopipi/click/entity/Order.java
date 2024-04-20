package shopipi.click.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.models.ShopOrderItemsModel;
import shopipi.click.utils._enum.StateOrderEnum;
import shopipi.click.utils._enum.TypePayment;

@Document(collection = "Orders")
@Data
@Builder
public class Order {
  @Id
  private String id;
  @CreatedBy
  private User user;
  private String shippingAddress;
  private Double totalOrder;
  private Double totalShipping;
  private Double totalDiscount;
  private Double totalCheckout;
  private Double capital; // vốn
  private Double revenue; // doanh thu
  private Double profit; // lợi nhuận
  private List<ShopOrderItemsModel> items;
  @Default
  private String payment = TypePayment.CASH.name();
  @Default
  private String state = StateOrderEnum.PENDING.name();
  private String note;
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createAt;
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @LastModifiedDate
  private LocalDateTime updateAt;

}
