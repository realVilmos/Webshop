package hu.vilmosdev.Webshop.ShopItem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ShopItemController {

  private final ShopItemService shopItemService;

  @GetMapping("/random-items")
  public List<ShopItem> getRandomItems(){
    return shopItemService.getRandomItems();
  }

  @GetMapping("/categories")
  public List<ShopItem> getItemsByCategory(@RequestParam List<String> category){
    System.out.println(category);
    return shopItemService.findByCategory(category);
  }

  @GetMapping("/id/{id}")
  public ShopItem getItemById(@PathVariable Long id){
    return shopItemService.getShopItemById(id);
  }


}
