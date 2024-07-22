package shopipi.click.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Category;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.CategoryService;
import shopipi.click.utils.Constants.HASROLE;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
  final CategoryService cateService;

  @Operation(summary = "add category")
  @PreAuthorize(HASROLE.ADMIN)
  @PostMapping("")
  public ResponseEntity<Category> add(@RequestBody Category category) {
    return ResponseEntity.ok().body(cateService.addCategory(category));
  }

  @Operation(summary = "update category")
  @PreAuthorize(HASROLE.ADMIN)
  @PostMapping("/{id}")
  public ResponseEntity<MainResponse<Category>> update(@PathVariable String id, @RequestBody Category category) {
    return ResponseEntity.ok().body(MainResponse.oke(cateService.updateCategory(id, category)));
  }

  @Operation(summary = "get all category")
  @GetMapping("")
  public ResponseEntity<MainResponse<List<Category>>> findAll() {
    return ResponseEntity.ok().body(MainResponse.oke(cateService.findAll()));
  }

  @Operation(summary = "get all category")
  @GetMapping("/{slug}")
  public ResponseEntity<MainResponse<Category>> findBySug(@PathVariable String slug) {
    return ResponseEntity.ok().body(MainResponse.oke(cateService.findBySlug()));
  }

  @Operation(summary = "get all category")
  @DeleteMapping("/{id}")
  public ResponseEntity<MainResponse<Boolean>> delete(@PathVariable String id) {
    return ResponseEntity.ok().body(MainResponse.oke(cateService.deleteCategory(id)));
  }

}
