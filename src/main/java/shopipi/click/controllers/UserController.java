package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.lang.Arrays;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.OnlineStatusUser;
import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.AddressModel;
import shopipi.click.models.paramsRequest.UserParamReq;
import shopipi.click.models.request.ChangePasswordReq;
import shopipi.click.models.request.RegisterReq;
import shopipi.click.models.response.LoginRes;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.OnlineStatusRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.UserService;
import shopipi.click.utils.Constants.HASROLE;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Optional;

import org.modelmapper.internal.util.Lists;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User")
public class UserController {
  private final UserService userService;
  private final OnlineStatusRepo onlineStatusRepo;

  @GetMapping("/online/{id}")
  public ResponseEntity<MainResponse<OnlineStatusUser>> online(@PathVariable String id) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(onlineStatusRepo.findByUserId(id).orElseThrow(() -> new NotFoundError("not found"))));
  }

  @GetMapping("/online/many")
  public ResponseEntity<MainResponse<List<OnlineStatusUser>>> online(@RequestParam List<String> ids) {
    List<OnlineStatusUser> onlineStatusUsers = onlineStatusRepo.findAllByUserId(ids);
    return ResponseEntity.ok(MainResponse.oke(onlineStatusUsers));
  }

  @PostMapping("/profile")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<User>> getInfo(@AuthenticationPrincipal UserRoot userRoot) {
    return ResponseEntity.ok().body(MainResponse.oke("get current profile user success", userRoot.getUser()));
  }

  @Operation(summary = "get all user")
  @PostMapping("")
  @PreAuthorize(HASROLE.ADMIN)
  public ResponseEntity<MainResponse<PageCustom<User>>> getAll(
      @PageableDefault(page = 0, size = 10, direction = Direction.ASC, sort = "id") Pageable pageable,
      @Valid @ModelAttribute UserParamReq userParamRequest) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findAll(pageable, userParamRequest)));
  }

  @Operation(summary = "get all user")
  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<User>>> getAlls(
      @PageableDefault(page = 0, size = 10, direction = Direction.ASC, sort = "id") Pageable pageable,
      @Valid @ModelAttribute UserParamReq userParamRequest) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findAll(pageable, userParamRequest)));
  }

  @Operation(summary = "get user by id")
  @PostMapping("/{id}")
  public ResponseEntity<MainResponse<User>> getOne(@PathVariable String id) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findUserById(id)));
  }

  @PostMapping("/convert-mod-to-user/{id}")
  public ResponseEntity<MainResponse<LoginRes>> convertModToUser(
      HttpServletRequest req,
      @PathVariable String id,
      @RequestBody @Valid RegisterReq registerReq) {
    String ipAddress = req.getHeader("X-Forwarded-For");
    if (ipAddress == null)
      ipAddress = req.getRemoteAddr();
    return ResponseEntity.ok().body(MainResponse.oke(userService.convertModToUser(id, registerReq)));
  }

  @Operation(summary = "convert user to shop")
  @PostMapping("/convert-user-to-shop/{id}")
  @PreAuthorize(HASROLE.USER)
  public ResponseEntity<MainResponse<User>> convertUserToShop(@PathVariable String id) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.convertUserToShop(id)));
  }

  @Operation(summary = "handle change password")
  @PostMapping("/change-password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Boolean>> changePassword(@AuthenticationPrincipal UserRoot userRoot,
      @Valid @RequestBody ChangePasswordReq changePasswordReq) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.handleChangePassword(userRoot.getUser(), changePasswordReq)));
  }

  // shop controller
  @Operation(summary = "get user by id")
  @GetMapping("/{id}")
  public ResponseEntity<MainResponse<User>> getShop(@PathVariable String id) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findUserById(id)));
  }

  @Operation(summary = "handle follow user")
  @PostMapping("/follow/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Boolean>> followUser(@PathVariable String id,
      @AuthenticationPrincipal UserRoot userRoot) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.handleFollowUser(userRoot.getUser(), id)));
  }

  @Operation(summary = "update user with file")
  @PostMapping("/update/file/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<User>> updateUserWithFile(@PathVariable String id,
      @RequestPart("name") String name,
      @RequestPart("email") String email,
      @RequestPart("phone") String phone,
      @RequestPart("slug") String slug,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.updateUserWithFile(id, name, email, phone, slug, image)));
  }

  @Operation(summary = "add address")
  @PostMapping("/add-address")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<AddressModel>> addAddress(@AuthenticationPrincipal UserRoot userRoot,
      @Valid @RequestBody AddressModel address) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.addAddress(userRoot.getUser(), address)));
  }

  @Operation(summary = "delete address")
  @DeleteMapping("/delete-address/{index}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Boolean>> deleteAddress(@AuthenticationPrincipal UserRoot userRoot,
      @PathVariable Integer index) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.deleteAddress(userRoot.getUser(), index)));
  }

  @Operation(summary = "update address")
  @PostMapping("/update-address/{index}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<AddressModel>> updateAddress(@AuthenticationPrincipal UserRoot userRoot,
      @Valid @RequestBody AddressModel address, @PathVariable Integer index) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.updateAddress(userRoot.getUser(), address, index)));
  }

  // @GetMapping("/cc")
  // public String getMethodName() {
  // userService.find();
  // return "ok";
  // }

  @GetMapping("/slug/{slug}")
  public ResponseEntity<MainResponse<User>> getShopBySlug(@PathVariable String slug) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findUserBySlug(slug)));
  }

}
