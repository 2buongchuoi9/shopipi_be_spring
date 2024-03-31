package shopipi.click.entity;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRoot implements UserDetails, OAuth2User {
  private User user;
  private Collection<? extends GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.user.getRoles().stream().map(v -> new SimpleGrantedAuthority(v.name())).toList();
  }

  @Override
  public String getPassword() {
    return this.user.getPassword();
  }

  @Override
  public String getUsername() {
    return this.user.getEmail();

  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public String getName() {
    return this.user.getName();
  }

  void print() {
    System.out.println(this.authorities);
  }

  public static UserRoot create(User user) {
    return UserRoot.builder()
        .user(user)
        .authorities(
            user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()))
        .build();
  }

  public static UserRoot create(User user, Map<String, Object> attributes) {
    UserRoot userRoot = UserRoot.create(user);
    userRoot.setAttributes(attributes);
    return userRoot;
  }
}
