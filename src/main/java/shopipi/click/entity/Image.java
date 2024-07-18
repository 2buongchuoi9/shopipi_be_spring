package shopipi.click.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.FileTypeEnum;

@Document(collection = "Images")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

  private String id;
  private String publicId;
  private String url;
  private String description;

  private long size;

  private String extension;

  private String mimeType;

  @Default
  private String type = FileTypeEnum.IMAGE.name();

  @CreatedBy
  private User createdBy;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

  public Image(String publicId, String url) {
    this.publicId = publicId;
    this.url = url;
  }
}
