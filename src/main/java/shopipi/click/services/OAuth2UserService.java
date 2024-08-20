package shopipi.click.services;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.repositories.UserRepo;
import shopipi.click.security.oauth2User.OAuth2UserInfo;
import shopipi.click.utils._enum.AuthTypeEnum;

@Server
@SuppressWarnings("null")
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {
  private UserRepo userRepo;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
    return processOAuth2User(oAuth2UserRequest, oAuth2User);
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo
        .getOAuth2User(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());

    Optional<User> userOptional = userRepo.findByEmail(oAuth2UserInfo.getEmail());
    User user;
    if (userOptional.isPresent()) {
      user = userOptional.get();
      user = updateExistingUser(user, oAuth2UserInfo);
    } else {
      user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
    }
    return UserRoot.create(user, oAuth2User.getAttributes());
  }

  private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
    return userRepo.save(User.builder()
        .authType(AuthTypeEnum.valueOf(oAuth2UserRequest.getClientRegistration().getClientName().toUpperCase()))
        .oAuth2Id(oAuth2UserInfo.getId())
        .name(oAuth2UserInfo.getName())
        .email(oAuth2UserInfo.getEmail())
        .image(oAuth2UserInfo.getImageUrl())
        .build());
  }

  private User updateExistingUser(User user, OAuth2UserInfo oAuth2UserInfo) {
    user.setName(oAuth2UserInfo.getName());
    if (oAuth2UserInfo.getImageUrl() != null)
      user.setImage(oAuth2UserInfo.getImageUrl());
    return userRepo.save(user);
  }

}
