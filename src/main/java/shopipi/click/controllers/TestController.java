package shopipi.click.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("")
  public ResponseEntity<?> test() {
    return ResponseEntity.ok().body(Map.of("project", "shopipi", "hello", "oke"));
  }
}
