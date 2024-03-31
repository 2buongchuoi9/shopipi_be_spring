package shopipi.click.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image {

  private String id;
  private String publicId;
  private String url;
  private String description;

  public Image(String publicId, String url) {
    this.publicId = publicId;
    this.url = url;
  }
}
