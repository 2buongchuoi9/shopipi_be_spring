package shopipi.click.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Notification;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.NotifyService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notify")
public class NotifyController {
  final NotifyService notifyService;

  @GetMapping("/{userId}")
  public ResponseEntity<MainResponse<PageCustom<Notification>>> getAll(@PathVariable String userId,
      @PageableDefault(page = 0, size = 10, direction = Direction.DESC, sort = "createdAt") Pageable pageable) {
    return ResponseEntity.ok(MainResponse.oke(notifyService.getNotificationsByUserId(userId, pageable)));

  }

}
