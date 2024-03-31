package shopipi.click.utils;

public final class Constants {
  public static final String URL_REDIRECT_OAUTH2_CLIENT = "redirect_url";
  public static final String OAUTH_COOKIE_NAME = "oauth2_auth_request";

  public final class HEADER {
    public static final String X_CLIENT_ID = "x-client-id";
    public static final String AUTHORIZATION = "authorization";
    public static final String REFRESHTOKEN = "x-rtoken-id";
  }

  public final class HASROLE {
    public static final String ADMIN = "hasAuthority('ADMIN')";
    public static final String USER = "hasAuthority('USER')";
    public static final String OWNER = "hasAuthority('OWNER')";
  }

}
