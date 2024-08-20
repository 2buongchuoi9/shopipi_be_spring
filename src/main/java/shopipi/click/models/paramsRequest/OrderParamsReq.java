package shopipi.click.models.paramsRequest;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;
import shopipi.click.configs.WebMvcConfig;

@Data
@Builder
public class OrderParamsReq {
  private String userId;
  private String shopId;
  private String state;

  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime endDate;

  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime startDate;
}
