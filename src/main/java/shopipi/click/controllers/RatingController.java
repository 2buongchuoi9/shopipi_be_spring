package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Comment;
import shopipi.click.entity.Rating;
import shopipi.click.entity.UserRoot;
import shopipi.click.models.paramsRequest.CommentParamsReq;
import shopipi.click.models.paramsRequest.RatingParamsReq;
import shopipi.click.models.request.RatingReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.UserRepo;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.RatingService;
import shopipi.click.utils.Constants.HASROLE;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rating")
public class RatingController {
  private final RatingService ratingService;

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Rating>>> getRatings(
      @PageableDefault(size = 100, page = 0, sort = "createdAt,desc") Pageable pageable,
      @ModelAttribute RatingParamsReq params) {
    return ResponseEntity.ok().body(MainResponse.oke(ratingService.findRatings(
        pageable, params)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("")
  public ResponseEntity<MainResponse<Rating>> addRating(
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid RatingReq ratingReq) {
    return ResponseEntity.ok().body(MainResponse.oke(ratingService.addRating(userRoot.getUser(), ratingReq)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/file")
  public ResponseEntity<MainResponse<Rating>> addRatingWithFile(
      @RequestParam("productId") String productId,
      @RequestParam("variantId") String variantId,
      @RequestParam("value") int value,
      @RequestParam("comment") String comment,
      @RequestParam(value = "images", required = false) List<MultipartFile> images,
      @AuthenticationPrincipal UserRoot userRoot) {
    return ResponseEntity.ok().body(
        MainResponse
            .oke(ratingService.addRatingWithFile(userRoot.getUser(), productId, variantId, value, comment, images)));
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

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/like/{ratingId}")
  public ResponseEntity<MainResponse<Rating>> like(
      @AuthenticationPrincipal UserRoot userRoot,
      @PathVariable String ratingId) {

    return ResponseEntity.ok().body(MainResponse.oke(
        ratingService.likeRating(userRoot.getUser(),
            ratingId)));
  }

  @GetMapping("/countRating/shop/{shopId}")
  public ResponseEntity<MainResponse<Integer>> countRatingByShop(
      @PathVariable String shopId) {

    return ResponseEntity.ok().body(MainResponse.oke(ratingService.countRatingByShopId(shopId)));
  }

}
