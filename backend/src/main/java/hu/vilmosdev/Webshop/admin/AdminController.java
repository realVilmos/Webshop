package hu.vilmosdev.Webshop.admin;
import hu.vilmosdev.Webshop.Item.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

  private final ItemService itemService;
  private final VendorService vendorService;

  @PostMapping("create-item")
  public ResponseEntity<String> register(@RequestBody ItemCreationRequest request){
    itemService.createShopItem(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("create-vendor")
  public ResponseEntity<String> register(@RequestBody Vendor vendor){
    vendorService.saveVendor(vendor);
    return ResponseEntity.ok().build();
  }

}
