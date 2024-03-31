package shopipi.click.security.oauth2User;

import java.util.Map;

public abstract class OAuth2UserInfo {
  public final static OAuth2UserInfo getOAuth2User(String registrationId, Map<String, Object> attributes) {
    return switch (registrationId) {
      case "Google" -> new GoogleUserInfo(attributes);
      case "FaceBook" -> new GoogleUserInfo(attributes);
      default -> new GoogleUserInfo(attributes);
    };
  }

  protected Map<String, Object> attributes;

  public OAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public abstract String getId();

  public abstract String getName();

  public abstract String getEmail();

  public abstract String getImageUrl();
}
