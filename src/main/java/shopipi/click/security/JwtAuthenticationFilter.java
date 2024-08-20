package shopipi.click.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// import vncafe.news.entities.KeyToken;
// import vncafe.news.exceptions.UnauthorizeError;
// import vncafe.news.repositories.KeyTokenRepo;
// import vncafe.news.security.UserRootService;
// import vncafe.news.utils.Constants.HEADER;
import shopipi.click.entity.KeyToken;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.NoAuthorizeError;
import shopipi.click.repositories.KeyTokenRepo;
import shopipi.click.services.JwtService;
import shopipi.click.services.UserRootService;
import shopipi.click.utils.Constants.HEADER;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  @Autowired
  private JwtService jwtService;
  @Autowired
  private KeyTokenRepo keyRepo;
  @Autowired
  private UserRootService userRootService;

  @Autowired
  @Qualifier("handlerExceptionResolver")
  private HandlerExceptionResolver exceptionResolver;

  final private List<String> list = Arrays.asList(
      "/api/auth",
      "/api/cart/add-to-cart/by-user-mod/",
      "/api/order/checkout-review-guest/",
      "/v2/api-docs",
      "/v3/api-docs",
      "/v3/api-docs",
      "/swagger-resources",
      "/swagger-resources",
      "/configuration/ui",
      "/configuration/security",
      "/swagger-ui",
      "/swagger-ui.html");

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest req,
      @NonNull HttpServletResponse res,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    try {

      if (req.getMethod().equals("GET") || checkRequest(req)) {
        System.out.println("qua");
        filterChain.doFilter(req, res);
        return;
      }

      // check x-client-id in header
      String clientId = req.getHeader(HEADER.X_CLIENT_ID);
      if (clientId == null)
        throw new NoAuthorizeError("x-client-id has must in header");

      KeyToken keyStore = keyRepo.findByUserId(clientId)
          .orElseThrow(() -> new NoAuthorizeError("invalid x-client-id in header"));

      // check authorization in header
      String token = req.getHeader(HEADER.AUTHORIZATION);
      if (token == null)
        throw new NoAuthorizeError("authorization has must in header");

      String userEmail = jwtService.verifyToken(token, jwtService.getPublicKeyFromString(keyStore.getPublicKey()));
      if (userEmail == null)
        throw new NoAuthorizeError("decode token is fail");

      UserDetails userDetails = userRootService.loadUserByUsername(userEmail);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          userDetails,
          null,
          userDetails.getAuthorities());

      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
      SecurityContextHolder.getContext().setAuthentication(authToken);

      filterChain.doFilter(req, res);
    } catch (Exception e) {
      System.out.println(":::::::::::::::::" + e.getMessage());
      exceptionResolver.resolveException(req, res, null, e);
    }
  }

  private boolean checkRequest(HttpServletRequest req) {
    String path = req.getServletPath();
    return list.stream().anyMatch(path::startsWith);
  }

}
