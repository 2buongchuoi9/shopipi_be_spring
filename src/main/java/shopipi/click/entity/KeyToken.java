package shopipi.click.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Document(collection = "KeyTokens")
@Data
@Builder
public class KeyToken implements Serializable {
  @Id
  private String id;

  private String userId;
  private String publicKey;
  private String refreshToken;

  // list of refresh token user used
  private List<String> refreshTokensUsed;

}