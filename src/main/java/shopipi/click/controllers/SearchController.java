package shopipi.click.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import shopipi.click.models.response.MainResponse;
import shopipi.click.models.response.SearchRes;
import shopipi.click.services.SearchService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
  final SearchService searchService;

  @Operation(summary = "search product and shop")
  @GetMapping("")
  public ResponseEntity<MainResponse<SearchRes>> search(@RequestParam String keySearch,
      @PageableDefault(size = 10, page = 0) Pageable pageable) {

    return ResponseEntity.ok().body(MainResponse.oke(searchService.searchProductAndShop(pageable,
        keySearch)));
  }
}
