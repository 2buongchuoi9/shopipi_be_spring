package shopipi.click.services;

import java.security.KeyPair;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import shopipi.click.applicationEvent.FollowEvent;
import shopipi.click.entity.Image;
import shopipi.click.entity.Notification;
import shopipi.click.entity.OnlineStatusUser;
import shopipi.click.entity.User;
import shopipi.click.entity.productSchema.Product;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.exceptions.DuplicateRecordError;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.AddressModel;
import shopipi.click.models.paramsRequest.UserParamReq;
import shopipi.click.models.request.ChangePasswordReq;
import shopipi.click.models.request.LoginReq;
import shopipi.click.models.request.RegisterReq;
import shopipi.click.models.response.LoginRes;
import shopipi.click.models.response.LoginRes.TokenStore;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.utils.Constants;
import shopipi.click.utils._enum.AuthTypeEnum;
import shopipi.click.utils._enum.NotificationType;
import shopipi.click.utils._enum.UserRoleEnum;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@Service
@RequiredArgsConstructor
public class UserService {
  final private UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final KeyTokenService keyTokenService;
  private final MongoTemplate mongoTemplate;
  private final ApplicationEventPublisher eventPublisher;

  private final ImageService fileService;

  public LoginRes registerUserLocal(RegisterReq registerReq) {
    // check email
    if (userRepo.existsByEmail(registerReq.getEmail()))
      throw new BabRequestError("user is registered");

    User user = userRepo.save(User.builder()
        .name(registerReq.getName())
        .email(registerReq.getEmail())
        .slug(registerReq.getEmail().toLowerCase().replace("[.@]", "-"))
        .roles(Set.of(UserRoleEnum.USER))
        .password(passwordEncoder.encode(registerReq.getPassword()))
        // .addressShipping(registerReq.getAddress())
        .image(registerReq.getImage() != null ? registerReq.getImage() : null)
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

  // public void find() {
  // List<User> list = userRepo.findAll();

  // list.forEach(v -> {
  // mongoTemplate.upsert(new Query(Criteria.where("userId").is(v.getId())), new
  // Update().set("isOnline", false)
  // .set("time", LocalDateTime.now()), OnlineStatusUser.class);
  // });

  // }

  public PageCustom<User> findAll(Pageable pageable, UserParamReq paramRequest) {
    String keySearch = paramRequest.getKeySearch();
    Boolean status = paramRequest.getStatus();
    Boolean verify = paramRequest.getVerify();
    String authType = paramRequest.getAuthType();
    String role = paramRequest.getRole();

    Query query = new Query();

    // Kiểm tra và thêm điều kiện keySearch
    if (keySearch != null && !keySearch.isEmpty()) {
      String regexPattern = ".*" + keySearch.trim() + ".*";
      query.addCriteria(new Criteria().orOperator(
          Criteria.where("name").regex(regexPattern, "i"),
          Criteria.where("email").regex(regexPattern, "i")));
    }

    // Kiểm tra và thêm điều kiện status
    if (status != null) {
      query.addCriteria(Criteria.where("status").is(status));
    }

    // Kiểm tra và thêm điều kiện verify
    if (verify != null) {
      query.addCriteria(Criteria.where("verify").is(verify));
    }

    // Kiểm tra và thêm điều kiện authType
    if (authType != null) {
      query.addCriteria(Criteria.where("authType").is(authType));
    }

    // Kiểm tra và thêm điều kiện role
    if (role != null && !role.isEmpty()) {
      // query.addCriteria(new Criteria().andOperator(
      // Criteria.where("roles").in(role),
      // Criteria.where("roles").ne(UserRoleEnum.ADMIN)));
      query.addCriteria(Criteria.where("roles").in(role));
      // query.addCriteria(Criteria.where("roles").ne(UserRoleEnum.ADMIN));
    }

    // Thêm điều kiện roles không phải MOD
    // query.addCriteria(Criteria.where("roles").ne(UserRoleEnum.MOD));

    long total = mongoTemplate.count(query, User.class);
    query.with(pageable);

    List<User> list = mongoTemplate.find(query, User.class);

    return new PageCustom<>(PageableExecutionUtils.getPage(list, pageable, () -> total));
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

  public User convertUserToShop(String userId) {
    User foundUser = userRepo.findByIdAndRolesIn(userId, Set.of(UserRoleEnum.USER))
        .orElseThrow(() -> new NotFoundError("userId", userId));

    // add role shop
    foundUser.addRole(UserRoleEnum.SHOP);
    return userRepo.save(foundUser);
  }

  public User findById(String id) {
    return userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));
  }

  public Boolean handleFollowUser(User user, String id) {

    User foundShop = userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));

    if (foundShop.getFollowers().contains(user.getId())) {
      foundShop.getFollowers().remove(user.getId());
    } else {
      foundShop.getFollowers().add(user.getId());

      eventPublisher.publishEvent(new FollowEvent(this, Notification.builder()
          .userFrom(user)
          .userTo(foundShop.getId())
          .content(user.getName() + " đã theo dõi bạn")
          .notificationType(NotificationType.NEW_FOLLOW)
          .build()));
    }

    userRepo.save(foundShop);

    return true;

  }

  public User updateUserWithFile(String id, String name, String email, String phone, String slug, MultipartFile image) {
    User foundUser = userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));

    // check tồn tại slug khác
    if (userRepo.existsBySlugAndIdNot(slug, id))
      throw new DuplicateRecordError("slug", slug);

    if (image != null) {
      Image file = fileService.addImageAndFile(image);
      foundUser.setImage(file.getUrl());
    }

    foundUser.setName(name);
    foundUser.setEmail(email);
    foundUser.setPhone(phone);
    foundUser.setSlug(slug);

    User userSave = userRepo.save(foundUser);

    // Cập nhật tất cả các sản phẩm có trường shop là foundUser
    mongoTemplate.updateMulti(
        Query.query(Criteria.where("shop.id").is(foundUser.getId())),
        new org.springframework.data.mongodb.core.query.Update()
            .set("shop", userSave),
        Product.class);

    return userSave;
  }

  public boolean handleChangePassword(User user, ChangePasswordReq passwordReq) {

    if (!passwordEncoder.matches(passwordReq.getPassword(), user.getPassword()))
      throw new BabRequestError("Mật khẩu không đúng");

    user.setPassword(passwordEncoder.encode(passwordReq.getPasswordNew()));
    userRepo.save(user);

    return true;
  }

  public AddressModel addAddress(User user, AddressModel address) {

    address.setIsDefault(true);
    user.getAddress().add(address);

    userRepo.save(user);

    return address;
  }

  public AddressModel updateAddress(User user, AddressModel address, int index) {

    AddressModel foundAddress = user.getAddress().get(index);
    if (foundAddress == null)
      throw new NotFoundError("address not found");

    foundAddress.setProvince(address.getProvince());
    foundAddress.setDistrict(address.getDistrict());
    foundAddress.setPhone(address.getPhone());
    foundAddress.setName(address.getName());

    if (address.getIsDefault()) {
      user.getAddress().forEach(v -> v.setIsDefault(false));
    }

    foundAddress.setIsDefault(address.getIsDefault());
    user.updateAddress(foundAddress, index);

    userRepo.save(user);

    return foundAddress;
  }

  public boolean deleteAddress(User user, int index) {

    AddressModel foundAddress = user.getAddress().get(index);
    if (foundAddress == null)
      throw new NotFoundError("address not found");

    user.removeAddress(index);

    userRepo.save(user);

    return true;
  }

  public User findUserBySlug(String slug) {
    return userRepo.findBySlug(slug).orElseThrow(() -> new NotFoundError("user not found"));
  }

  public Map<String, Long> countProductInShops(List<String> shopIds) {
    Map<String, Long> map = new java.util.HashMap<>();
    shopIds.forEach(v -> {
      map.put(v, mongoTemplate.count(Query.query(Criteria.where("shop.id").is(v)), Product.class));
    });

    return map;
  }

  public Boolean changeStatus(String id) {
    User user = userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));
    user.setStatus(user.getStatus() ? false : true);
    userRepo.save(user);
    return true;
  }

  public PageCustom<User> findFollowUser(String id, Pageable pageable) {
    User user = userRepo.findById(id).orElseThrow(() -> new NotFoundError("user not found"));

    Query query = new Query();
    query.addCriteria(Criteria.where("id").in(user.getFollowers()));

    query.with(pageable);

    List<User> list = mongoTemplate.find(query, User.class);
    long total = mongoTemplate.count(query, User.class);

    return new PageCustom<User>(PageableExecutionUtils.getPage(list, pageable, () -> total));

  }

}
