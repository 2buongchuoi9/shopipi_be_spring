package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import shopipi.click.entity.User;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.paramsRequest.UserParamReq;
import shopipi.click.models.request.RegisterReq;
import shopipi.click.models.response.LoginRes;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.UserService;
import shopipi.click.utils.Constants.HASROLE;
import java.nio.file.attribute.UserPrincipal;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "User")
public class UserController {
  private final UserService userService;

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

  // @Operation(summary = "handle change password")
  // @PostMapping("/change-password")
  // @PreAuthorize(HASROLE.USER + " or " + HASROLE.USER)
  // public ResponseEntity<MainResponse<Boolean>> changePassword(
  // @Valid @RequestBody ChangePasswordReq changePasswordReq) {

  // return ResponseEntity.ok()
  // .body(MainResponse.oke(userService.handleChangePassword(changePasswordReq)));
  // }

  // shop controller
  @Operation(summary = "get user by id")
  @GetMapping("/{id}")
  public ResponseEntity<MainResponse<User>> getShop(@PathVariable String id) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(userService.findUserById(id)));
  }

}
