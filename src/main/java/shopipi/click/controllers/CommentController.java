package shopipi.click.controllers;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Comment;
import shopipi.click.entity.UserRoot;
import shopipi.click.exceptions.BabRequestError;
import shopipi.click.models.paramsRequest.CommentParamsReq;
import shopipi.click.models.request.CommentReq;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.CommentService;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
  final CommentService commentService;

  @PostMapping("")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<MainResponse<Comment>> add(
      @RequestBody @Valid CommentReq commentReq) {
    return ResponseEntity.ok().body(MainResponse.oke(commentService.add(commentReq)));
  }

  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Comment>>> getComments(
      @PageableDefault(size = 100, page = 0, sort = "createdAt,desc") Pageable pageable,
      @ModelAttribute CommentParamsReq params) {
    return ResponseEntity.ok().body(MainResponse.oke(commentService.findComments(params,
        pageable)));
  }

  @Operation(summary = "Update comment just need content")
  @PostMapping("/{commentId}")
  public ResponseEntity<MainResponse<Comment>> updateComment(
      @PathVariable String commentId,
      @AuthenticationPrincipal UserRoot userRoot,
      @RequestBody @Valid CommentReq commentReq) {

    return ResponseEntity.ok()
        .body(MainResponse.oke(commentService.updateComment(userRoot.getUser(), commentId, commentReq)));
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/like/{commentId}")
  public ResponseEntity<MainResponse<Comment>> like(
      @AuthenticationPrincipal UserRoot userRoot,
      @PathVariable String commentId) {

    return ResponseEntity.ok().body(MainResponse.oke(
        commentService.likeComment(userRoot.getUser(),
            commentId)));
  }

  @PreAuthorize("isAuthenticated()")
  @DeleteMapping("/{commentId}")
  public ResponseEntity<MainResponse<Boolean>> delete(@AuthenticationPrincipal UserRoot userRoot,
      @PathVariable String commentId) {
    return ResponseEntity.ok().body(MainResponse.oke(commentService.deleteComment(userRoot.getUser(),
        commentId)));
  }

}
