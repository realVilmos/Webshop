package hu.vilmosdev.Webshop.ShopItem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopItemService {
  private final ShopItemRepository shopItemRepository;
  public List<ShopItem> getRandomItems(){
    //To implement
    return null;
  }

  public List<ShopItem> findByCategory(List<String> categoriesList) {
    return shopItemRepository.findByCategoriesIn(categoriesList);
  }

  public ShopItem getShopItemById(Long id){
    return shopItemRepository.getReferenceById(id);
  }

  public void createShopItem(ItemRequest request){
    var shopItem = ShopItem.builder()
      .name(request.getName())
      .categories(request.getCategories())
      .description(request.getDescription())
      .imgUrl(request.getImgUrl())
      .build();

    shopItemRepository.save(shopItem);
  }


}
