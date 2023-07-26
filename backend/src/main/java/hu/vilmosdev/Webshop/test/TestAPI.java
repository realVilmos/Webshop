package hu.vilmosdev.Webshop.test;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loggedin")
@RequiredArgsConstructor
public class TestAPI {
  @GetMapping("")
  public ResponseEntity register(){
    return ResponseEntity.ok().build();
  }

}
