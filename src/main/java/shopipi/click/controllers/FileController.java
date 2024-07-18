package shopipi.click.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Image;
import shopipi.click.exceptions.NotFoundError;
import shopipi.click.models.response.MainResponse;
import shopipi.click.repositories.repositoryUtil.PageCustom;
import shopipi.click.services.ImageService;
import shopipi.click.utils.Constants.HASROLE;
import shopipi.click.utils._enum.FileTypeEnum;

import java.util.Arrays;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
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

  private final ImageService fileService;

  @Operation(summary = "up load image")
  @PostMapping("/upload-image")
  public ResponseEntity<MainResponse<Image>> uploadImage(@RequestParam(name = "file") MultipartFile multipartFile) {
    return ResponseEntity.ok().body(MainResponse.oke(fileService.addImageAndFile(multipartFile)));
  }

  @Operation(summary = "up load video")
  @PostMapping("/upload-video")
  public ResponseEntity<MainResponse<Image>> uploadVideo(@RequestParam(name = "file") MultipartFile multipartFile) {
    // return
    // ResponseEntity.ok().body(MainResponse.oke(fileService.addVideoFile(multipartFile)));
    return ResponseEntity.ok().body(MainResponse.oke(fileService.addVideoFile(multipartFile)));
  }

  // @Operation(summary = "get all image")
  // @GetMapping("/image")
  // public ResponseEntity<MainResponse<PageCustom<Image>>> getAllImage(
  // @PageableDefault(size = 20, page = 0) Pageable pageable) {
  // return
  // ResponseEntity.ok().body(MainResponse.oke(fileService.findAll(pageable)));
  // }

  @Operation(summary = "get all file")
  @GetMapping("")
  public ResponseEntity<MainResponse<PageCustom<Image>>> getAllImage(
      @RequestParam(name = "type", defaultValue = "ALL") String type,
      @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
    // check type in FileTypeEnum
    if (!Arrays.asList(FileTypeEnum.values()).stream().map(FileTypeEnum::name).anyMatch(type::equals))
      throw new NotFoundError("type not found");

    return ResponseEntity.ok().body(MainResponse.oke(fileService.findAll(pageable, type)));
  }

  @PreAuthorize(HASROLE.ADMIN)
  @Operation(summary = "delete image")
  @DeleteMapping("/image/{id}")
  public ResponseEntity<Boolean> deleteImage(@PathVariable String id) {
    return ResponseEntity.ok().body(fileService.delete(id));
  }

}
