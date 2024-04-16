package shopipi.click.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Comment;
import shopipi.click.models.request.CommentReq;
import shopipi.click.models.response.MainResponse;
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

  // @GetMapping("")
  // public ResponseEntity<MainResponse<List<Comment>>> getComments(
  // @PageableDefault(size = 100, page = 0, sort = "createAt,desc") Pageable
  // pageable,
  // @ModelAttribute CommentParamsReq params) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(commentService.getComments(params,
  // pageable)));
  // }

  // @PostMapping("/like/{commentId}")
  // public ResponseEntity<MainResponse<Comment>> like(@AuthenticationPrincipal
  // UserRoot userRoot,
  // @PathVariable String commentId) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(commentService.likeComment(userRoot.getUser(),
  // commentId)));
  // }

  // @DeleteMapping("/{commentId}")
  // @PreAuthorize("isAuthenticated()")
  // public ResponseEntity<MainResponse<Boolean>> delete(@AuthenticationPrincipal
  // UserRoot userRoot,
  // @PathVariable String commentId) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(commentService.deleteComment(userRoot.getUser(),
  // commentId)));
  // }

}
