package shopipi.click.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.TypeDiscount;

@Document(collection = "Discounts")
@Data
@Builder
public class Discount {
  @Id
  private String id;
  @CreatedBy
  private User shop;
  private String name;
  private String code;

  @Default
  private String type = TypeDiscount.FIXED_AMOUNT.name();

  @Default
  private Double value = 0.0; // neu type=PERCENTAGE_AMOUNT thi tinh theo %(vidu: 34.5 => 34.5%)
  @Default
  private Integer totalCount = 50; // so luong discount co the su dung
  @Default
  private Double minOrderValue = null; // gia tri toi thieu de ap dung discount
  @Default
  private List<String> userUsedIds = new ArrayList<>(); // danh sach user da dung discount
  @Default
  private Integer countUserUseDiscount = 1; // so lan su dung cua moi user
  @Default
  private Boolean status = true;
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateStart;
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime dateEnd;
}
