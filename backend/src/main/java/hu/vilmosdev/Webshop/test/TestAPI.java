package hu.vilmosdev.Webshop.test;

import hu.vilmosdev.Webshop.ShopItem.ItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loggedin")
@RequiredArgsConstructor
public class TestAPI {
  @GetMapping("")
  public ResponseEntity register(@RequestBody ItemRequest request){
    return ResponseEntity.ok().build();
  }

}
