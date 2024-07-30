package shopipi.click.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import shopipi.click.entity.Brand;
import shopipi.click.models.response.MainResponse;
import shopipi.click.services.BrandService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brand")
public class BrandController {
  final BrandService brandService;

  @GetMapping("")
  public ResponseEntity<MainResponse<List<Brand>>> find(@RequestParam(required = false) String keySearch) {
    return ResponseEntity.ok(MainResponse.oke(brandService.find(keySearch)));
  }

  @PostMapping("")
  public ResponseEntity<MainResponse<Brand>> add(@RequestBody @Valid Brand brand) {
    return ResponseEntity.ok(MainResponse.oke(brandService.addBrand(brand)));
  }

}
