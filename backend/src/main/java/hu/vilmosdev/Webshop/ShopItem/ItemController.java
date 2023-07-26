package hu.vilmosdev.Webshop.ShopItem;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

  private final ItemService shopItemService;

  @GetMapping(value = "/random", params = {"page", "size"})
  public ResponseEntity<Page<ReducedItemResponse>> getItems(@RequestParam("page") int page,
                                           @RequestParam("size") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return shopItemService.findRandom(pageable);
  }

  @GetMapping(params = {"page", "size", "categories"})
  public ResponseEntity<Page<ReducedItemResponse>> getItemsByCategory(@RequestParam("page") int page,
                                       @RequestParam("size") int size,
                                       @RequestParam("categories") List<String> categories) {
    Pageable pageable = PageRequest.of(page, size);
    return shopItemService.findByCategoryIn(categories, pageable);
  }

  @GetMapping(params = {"page", "size", "vendors"})
  public ResponseEntity<Page<ReducedItemResponse>> getItemsByVendor(@RequestParam("page") int page,
                                     @RequestParam("size") int size,
                                     @RequestParam("vendors") List<String> vendors) {
    Pageable pageable = PageRequest.of(page, size);
    return shopItemService.findByVendorIn(vendors, pageable);
  }

  @GetMapping(params = {"page", "size", "categories", "vendors"})
  public ResponseEntity<Page<ReducedItemResponse>> getItemsByCategoryAndVendor(@RequestParam("page") int page,
                                                @RequestParam("size") int size,
                                                @RequestParam("categories") List<String> categories,
                                                @RequestParam("vendors") List<String> vendors) {
    Pageable pageable = PageRequest.of(page, size);
    return shopItemService.findByCategoryInAndVendorIn(categories, vendors, pageable);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable Long id) {
    return shopItemService.findById(id);
  }

}
