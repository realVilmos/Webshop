package hu.vilmosdev.Webshop.admin;
import hu.vilmosdev.Webshop.ShopItem.ItemRequest;
import hu.vilmosdev.Webshop.ShopItem.ShopItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

  private final ShopItemService service;
  @PostMapping("create-item")
  public ResponseEntity register(@RequestBody ItemRequest request){
    service.createShopItem(request);
    return ResponseEntity.ok().build();
  }

}
