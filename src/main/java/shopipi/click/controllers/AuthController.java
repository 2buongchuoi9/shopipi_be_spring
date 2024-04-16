package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.User;
import shopipi.click.models.request.LoginReq;
import shopipi.click.models.request.RegisterReq;
import shopipi.click.models.response.LoginRes;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<MainResponse<LoginRes>> register(@RequestBody @Valid RegisterReq registerReq) {
    return ResponseEntity.ok().body(MainResponse.oke(userService.registerUserLocal(registerReq)));
  }

  @PostMapping("/login")
  public ResponseEntity<MainResponse<LoginRes>> login(@RequestBody @Valid LoginReq loginReq) {
    return ResponseEntity.ok().body(MainResponse.oke(userService.loginLocal(loginReq)));
  }

  @GetMapping("/create-user-mod")
  public ResponseEntity<MainResponse<User>> createUserMod(HttpServletRequest req) {
    String ipAddress = req.getHeader("X-Forwarded-For");
    if (ipAddress == null)
      ipAddress = req.getRemoteAddr();
    return ResponseEntity.ok().body(MainResponse.oke(userService.createUserMod(ipAddress)));
  }

}
