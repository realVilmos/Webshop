package hu.vilmosdev.Webshop.admin;
import hu.vilmosdev.Webshop.Item.*;
import hu.vilmosdev.Webshop.Item.Category.Category;
import hu.vilmosdev.Webshop.Item.Category.CategoryCreationRequest;
import hu.vilmosdev.Webshop.Item.Category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    return ResponseEntity.ok().build();
  }

  @PostMapping("create-vendor")
  public ResponseEntity<String> createVendor(@RequestBody Vendor vendor){
    vendorService.saveVendor(vendor);
    return ResponseEntity.ok().build();
  }

  @PostMapping("create-category")
  public ResponseEntity<Category> createCategory(@RequestBody CategoryCreationRequest request) {
    System.out.println(request);
    Category newCategory = categoryService.createCategory(request);
    return ResponseEntity.ok().body(newCategory);
  }

}
