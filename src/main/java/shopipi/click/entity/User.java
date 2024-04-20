package shopipi.click.entity;

import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.utils._enum.AuthTypeEnum;
import shopipi.click.utils._enum.UserRoleEnum;

@Document(collection = "Users")
@Data
@Builder
public class User {
  @Id
  private String id;
  private String name;
  private String email;
  private String image;

  @JsonIgnore
  private String password;

  @Default
  private Boolean status = true;

  @Default
  private Boolean verify = false;

  @Default
  private AuthTypeEnum authType = AuthTypeEnum.LOCAL;

  @Default
  private Set<UserRoleEnum> roles = Set.of(UserRoleEnum.USER);

  @Default
  private String oAuth2Id = null;

  @Default
  private String addressShipping = null;

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createAt;

  public void addRole(UserRoleEnum role) {
    if (this.roles == null)
      this.roles = Set.of(UserRoleEnum.USER, role);
    this.roles.add(role);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

}
