package hu.vilmosdev.Webshop.ShopItem;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository shopItemRepository;
  public List<Item> getRandomItems(){
    //To implement
    return null;
  }

  public Item getShopItemById(Long id){
    return shopItemRepository.getReferenceById(id);
  }

  public void createShopItem(ItemRequest request){

    ItemPrice price = ItemPrice.builder()
      .originalPrice(request.getOriginalPrice())
      .isOnSale(request.isOnSale())
      .salePrice(request.getSalePrice())
      .saleEndDate(request.getSaleEndDate())
      .build();

    var shopItem = Item.builder()
      .name(request.getName())
      .category(request.getCategory())
      .description(request.getDescription())
      .vendor(request.getVendor())
      .quantityInStock(request.getQuantityInStock())
      .imgUrl(request.getImgUrl())
      .weight(request.getWeight())
      .dimensions(request.getDimensions())
      .manufacturer(request.getManufacturer())
      .itemPrice(price)
      .build();

    shopItemRepository.save(shopItem);
  }


  public ResponseEntity<Page<ReducedItemResponse>> findByCategoryIn(List<String> categories, Pageable pageable) {
    Page<Item> items = shopItemRepository.findByCategoryIn(categories, pageable);
    return ResponseEntity.ok(reduceItemProperies(items, pageable));
  }

  public ResponseEntity<Page<ReducedItemResponse>> findByVendorIn(List<String> vendors, Pageable pageable) {
    Page<Item> items = shopItemRepository.findByVendorIn(vendors, pageable);
    return ResponseEntity.ok(reduceItemProperies(items, pageable));
  }

  public ResponseEntity<Page<ReducedItemResponse>> findByCategoryInAndVendorIn(List<String> categories, List<String> vendors, Pageable pageable) {
    Page<Item> items = shopItemRepository.findByCategoryInAndVendorIn(categories, vendors, pageable);
    return ResponseEntity.ok(reduceItemProperies(items, pageable));
  }

  public ResponseEntity<Page<ReducedItemResponse>> findRandom(Pageable pageable) {
    Page<Item> items = shopItemRepository.findRandom(pageable);
    return ResponseEntity.ok(reduceItemProperies(items, pageable));
  }

  public ResponseEntity<Item> findById(Long id) {
    Optional<Item> itemOptional = shopItemRepository.findById(id);

    if(itemOptional.isEmpty()){
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(itemOptional.get());
  }

  private Page<ReducedItemResponse> reduceItemProperies(Page<Item> items, Pageable pageable){
    List<ReducedItemResponse> reducedItemResponses = items.getContent().stream().map(item -> {
      ReducedItemResponse rir = new ReducedItemResponse();
      rir.setName(item.getName());
      rir.setItemPrice(item.getItemPrice());
      rir.setImgUrl(item.getImgUrl());
      rir.setRating(item.getRating());
      rir.setId(item.getId());

      return rir;
    }).toList();

    return new PageImpl<>(reducedItemResponses, pageable, items.getTotalElements());
  }
}
