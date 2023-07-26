package hu.vilmosdev.Webshop.admin;
import hu.vilmosdev.Webshop.ShopItem.ItemRequest;
import hu.vilmosdev.Webshop.ShopItem.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

  private final ItemService service;
  @PostMapping("create-item")
  public ResponseEntity<String> register(@RequestBody ItemRequest request){
    System.out.println(request);
    service.createShopItem(request);
    return ResponseEntity.ok().build();
  }


}
