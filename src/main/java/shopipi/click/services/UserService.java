package shopipi.click.services;

import java.security.KeyPair;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.User;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.paramsRequest.UserParamRequest;
import shopipi.click.models.request.LoginReq;
import shopipi.click.models.request.RegisterReq;
import shopipi.click.models.response.LoginRes;
import shopipi.click.models.response.LoginRes.TokenStore;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.utils.Constants;
import shopipi.click.utils._enum.AuthTypeEnum;
import shopipi.click.utils._enum.UserRoleEnum;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserService {
  final private UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final KeyTokenService keyTokenService;
  private final MongoTemplate mongoTemplate;

  public LoginRes registerUserLocal(RegisterReq registerReq) {
    // check email
    if (userRepo.existsByEmail(registerReq.getEmail()))
      throw new BabRequestError("user is registered");

    User user = userRepo.save(User.builder()
        .name(registerReq.getName())
        .email(registerReq.getEmail())
        .roles(Set.of(UserRoleEnum.USER))
        .password(passwordEncoder.encode(registerReq.getPassword()))
        .build());

    KeyPair keys = JwtService.generatorKeyPair();

    TokenStore tokens = new TokenStore(jwtService.createAccessToken(user.getEmail(), keys.getPrivate()),
        jwtService.createRefreshToken(user.getEmail(), keys.getPrivate()));

    if (!keyTokenService.createKeyStore(
        user.getId(),
        jwtService.getStringFromPublicKey(keys.getPublic()),
        tokens.getRefreshToken()))
      throw new RuntimeException("fail to create keyStore");

    return new LoginRes(tokens, user);
  }

  public LoginRes loginLocal(LoginReq loginReq) {
    // check email password
    User foundUser = userRepo.findByEmail(loginReq.getEmail())
        .orElseThrow(() -> new NotFoundError("user is not registered"));

    if (!passwordEncoder.matches(loginReq.getPassword(), foundUser.getPassword()))
      throw new BabRequestError("password is not true");

    KeyPair keys = JwtService.generatorKeyPair();

    TokenStore tokens = new TokenStore(jwtService.createAccessToken(loginReq.getEmail(), keys.getPrivate()),
        jwtService.createRefreshToken(loginReq.getEmail(), keys.getPrivate()));

    if (!keyTokenService.createKeyStore(
        foundUser.getId(),
        jwtService.getStringFromPublicKey(keys.getPublic()),
        tokens.getRefreshToken()))
      throw new RuntimeException("fail to create keyStore");
    return new LoginRes(tokens, foundUser);
  }

  public User findUserById(String id) {
    return userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));
  }

  public PageCustom<User> findAll(Pageable pageable, UserParamRequest paramRequest) {
    String keySearch = paramRequest.getKeySearch();
    Boolean status = paramRequest.getStatus();
    Boolean verify = paramRequest.getVerify();
    String authType = paramRequest.getAuthType();

    Query query = new Query();

    if (keySearch != null && !keySearch.isEmpty()) {
      String regexPattern = "(?i)" + keySearch.trim(); // Thêm ?i để không phân biệt chữ hoa chữ thường
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("name").regex(regexPattern),
          Criteria.where("email").regex(regexPattern)));
    }

    if (status != null)
      query.addCriteria(Criteria.where("status").is(status));

    if (verify != null)
      query.addCriteria(Criteria.where("verify").is(verify));

    if (authType != null)
      query.addCriteria(Criteria.where("authType").is(authType));

    query.with(pageable);

    List<User> list = mongoTemplate.find(query, User.class);
    long total = mongoTemplate.count(query, User.class);

    return new PageCustom<User>(PageableExecutionUtils.getPage(list, pageable, () -> total));
  }

  public User createUserMod(String ipAddress) {

    User user = userRepo.findByEmail(ipAddress + Constants.AFTER_EMAIL).orElse(
        User.builder()
            .name(ipAddress)
            .password(passwordEncoder.encode(ipAddress))
            .email(ipAddress + Constants.AFTER_EMAIL)
            .roles(Set.of(UserRoleEnum.MOD))
            .authType(AuthTypeEnum.LOCAL)
            .status(true)
            .verify(false)
            .build());

    return userRepo.save(user);
  }

  public LoginRes convertModToUser(String userModId, RegisterReq registerReq) {
    if (userRepo.existsByEmail(registerReq.getEmail()))
      throw new BabRequestError("user is registered");

    User foundUser = userRepo.findByIdAndRolesIn(userModId, Set.of(UserRoleEnum.MOD))
        .orElseThrow(() -> new NotFoundError("userModId", userModId));

    foundUser.setEmail(registerReq.getEmail());
    foundUser.setName(registerReq.getName());
    foundUser.setPassword(passwordEncoder.encode(registerReq.getPassword()));
    foundUser.setRoles(Set.of(UserRoleEnum.USER));

    foundUser = userRepo.save(foundUser);

    KeyPair keys = JwtService.generatorKeyPair();

    TokenStore tokens = new TokenStore(jwtService.createAccessToken(foundUser.getEmail(), keys.getPrivate()),
        jwtService.createRefreshToken(foundUser.getEmail(), keys.getPrivate()));

    if (!keyTokenService.createKeyStore(
        foundUser.getId(),
        jwtService.getStringFromPublicKey(keys.getPublic()),
        tokens.getRefreshToken()))
      throw new RuntimeException("fail to create keyStore");

    return new LoginRes(tokens, foundUser);

  }

}
