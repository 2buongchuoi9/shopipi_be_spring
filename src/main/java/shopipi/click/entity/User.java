package shopipi.click.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import shopipi.click.configs.WebMvcConfig;
import shopipi.click.models.AddressModel;
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
  private String slug;

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

  // @Default
  // private String addressShipping = null;

  private String phone;

  @Default
  private List<AddressModel> address = new ArrayList<>();

  @Default
  private Set<String> followers = new HashSet();

  @CreatedDate
  @DateTimeFormat(pattern = WebMvcConfig.dateTimeFormat)
  @JsonFormat(pattern = WebMvcConfig.dateTimeFormat)
  private LocalDateTime createdAt;

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

  public void addAddress(AddressModel addressModel) {
    if (this.address == null)
      this.address = new ArrayList<>();

    this.address.add(addressModel);
  }

  public void removeAddress(int index) {
    this.address.remove(index);
  }

  public void updateAddress(AddressModel addressModel, int index) {
    this.address.set(index, addressModel);
  }

}
