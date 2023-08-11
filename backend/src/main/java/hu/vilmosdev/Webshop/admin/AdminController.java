package hu.vilmosdev.Webshop.admin;

import hu.vilmosdev.Webshop.Item.Category.Category;
import hu.vilmosdev.Webshop.Item.Category.CategoryCreationRequest;
import hu.vilmosdev.Webshop.Item.Category.CategoryService;
import hu.vilmosdev.Webshop.Item.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/")
@RequiredArgsConstructor
public class AdminController {

  private final ItemService itemService;
  private final VendorService vendorService;
  private final CategoryService categoryService;
  private final ImageStorageService imageStorageService;

  @PostMapping(value = "create-item")
  public ResponseEntity<String> createItem(@RequestBody ItemCreationRequest request){
    itemService.createShopItem(request);

    return ResponseEntity.ok().body("{\"message\": \"creation_success\"}");
  }

  @PostMapping("create-vendor")
  public ResponseEntity<String> createVendor(@RequestBody Vendor vendor){
    vendorService.saveVendor(vendor);
    return ResponseEntity.ok().body("{\"message\": \"creation_success\"}");
  }

  @PostMapping("create-category")
  public ResponseEntity<Category> createCategory(@RequestBody CategoryCreationRequest request) {
    (request);
    Category newCategory = categoryService.createCategory(request);
    return ResponseEntity.ok().body(newCategory);
  }

}
