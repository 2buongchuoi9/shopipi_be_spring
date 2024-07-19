package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Rating;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.request.RatingReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.UserRepo;
import shopipi.click.services.RatingService;
import shopipi.click.utils.Constants.HASROLE;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {
  private final RatingService ratingService;

  // @PreAuthorize("isAuthenticated()")
  // @PostMapping("")
  // public ResponseEntity<MainResponse<Rating>> addRating(@RequestBody RatingReq
  // ratingReq) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(ratingService.createRating(ratingReq)));
  // }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("")
  public ResponseEntity<MainResponse<Rating>> addRating(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid RatingReq ratingReq) {
    return ResponseEntity.ok().body(MainResponse.oke(ratingService.addRating(userRoot.getUser(), ratingReq)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/{ratingId}")
  public ResponseEntity<MainResponse<Rating>> updateRating(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid RatingReq ratingReq, @PathVariable String ratingId) {
    return ResponseEntity.ok()
        .body(MainResponse.oke(ratingService.updateRating(ratingId, userRoot.getUser(), ratingReq)));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{ratingId}")
  public ResponseEntity<MainResponse<String>> deleteRating(
      @PathVariable String ratingId,
      @AuthenticationPrincipal UserRoot userRoot) {
    ratingService.deleteRating(ratingId);
    return ResponseEntity.ok().body(MainResponse.oke("Delete rating success"));
  }

}
