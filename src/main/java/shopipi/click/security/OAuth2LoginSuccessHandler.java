package shopipi.click.security;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.response.LoginRes.TokenStore;
import shopipi.click.services.JwtService;
import shopipi.click.services.KeyTokenService;
import shopipi.click.utils.Constants;
import shopipi.click.utils.CookieUtils;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

  @Value("${den.url_client}")
  private String url_client;

  final JwtService jwtService;
  final KeyTokenService keyTokenService;
  final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  public static final String OAUTH_COOKIE = Constants.OAUTH_COOKIE_NAME;
  public static final String REDIRECT_URL = Constants.URL_REDIRECT_OAUTH2_CLIENT;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    String targetUrl = getUrl(request, response, authentication);

    System.out.println("url:::::" + targetUrl);

    clearAuthenticationAttributes(request, response);
    getRedirectStrategy().sendRedirect(request, response, targetUrl);

  }

  private TokenStore loginWithOAuth2(User user) {

    KeyPair keys = JwtService.generatorKeyPair();

    TokenStore tokens = new TokenStore(jwtService.createAccessToken(user.getEmail(), keys.getPrivate()),
        jwtService.createRefreshToken(user.getEmail(), keys.getPrivate()));

    if (!keyTokenService.createKeyStore(
        user.getId(),
        jwtService.getStringFromPublicKey(keys.getPublic()),
        tokens.getRefreshToken()))
      throw new RuntimeException("fail to create keyStore");

    return tokens;
  }

  @SuppressWarnings("null")
  private String getUrl(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    Optional<String> redirectUri = CookieUtils
        .getCookie(request, Constants.URL_REDIRECT_OAUTH2_CLIENT)
        .map(Cookie::getValue);

    System.out.println("redirectUri::::::::::" + redirectUri.get());

    String targetUrl = redirectUri.orElse(url_client);
    System.out.println("redirect::::::::::" + redirectUri);

    User user = ((UserRoot) authentication.getPrincipal()).getUser();
    System.out.println("userssssssssssssssss: " + user);
    TokenStore tokenStore = loginWithOAuth2(user);

    return UriComponentsBuilder.fromUriString(targetUrl)
        .queryParam("userId", user.getId())
        .queryParam("accessToken", tokenStore.getAccessToken())
        .queryParam("refreshToken", tokenStore.getRefreshToken())
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }

}
