package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Image;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.ImageService;
import shopipi.click.utils.Constants.HASROLE;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

  private final ImageService imageService;

  @Operation(summary = "up load image")
  @PostMapping("/upload-image")
  public ResponseEntity<MainResponse<Image>> uploadImage(@RequestParam(name = "file") MultipartFile multipartFile) {
    return ResponseEntity.ok().body(MainResponse.oke(imageService.addImageAndFile(multipartFile)));
  }

  @Operation(summary = "get all image")
  @GetMapping("/image")
  public ResponseEntity<MainResponse<PageCustom<Image>>> getAllImage(
      @PageableDefault(size = 20, page = 0) Pageable pageable) {
    return ResponseEntity.ok().body(MainResponse.oke(imageService.findAll(pageable)));
  }

  @PreAuthorize(HASROLE.ADMIN)
  @Operation(summary = "delete image")
  @DeleteMapping("/image/{id}")
  public ResponseEntity<Boolean> deleteImage(@PathVariable String id) {
    return ResponseEntity.ok().body(imageService.delete(id));
  }

}
