package shopipi.click.configs;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;

@Component
@RequiredArgsConstructor

public class SecurityAuditorAware implements AuditorAware<User> {

  @SuppressWarnings("null")
  @Override
  public Optional<User> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .filter(principal -> !(principal instanceof String))
        .map(UserRoot.class::cast)
        .map(UserRoot::getUser);
  }

}