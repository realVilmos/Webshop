package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.Item.Category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ItemService {
  private final ItemRepository itemRepository;
  private final CategoryRepository categoryRepository;
  private final VendorRepository vendorRepository;
  private final ImageStorageService imageStorageService;

  private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

  public Item getShopItemById(Long id){
    return itemRepository.getReferenceById(id);
  }

  public Item createShopItem(ItemCreationRequest request){
    try{
      ItemPrice price = new ItemPrice();
      price.setOriginalPrice(request.getOriginalPrice());

      if(request.isOnSale()){
        price.setOnSale(true);
        price.setSalePrice(request.getSalePrice());
        price.setSaleEndDate(request.getSaleEndDate());
      }else{
        price.setOnSale(false);
      }

      Item item = Item.builder()
        .name(request.getName())
        .category(categoryRepository.getReferenceById(request.getCategory()))
        .description(request.getDescription())
        .vendor(vendorRepository.getReferenceById(request.getVendor()))
        .quantityInStock(request.getQuantityInStock())
        .weight(request.getWeight())
        .dimensions(request.getDimensions())
        .manufacturer(request.getManufacturer())
        .build();

      item.setItemPrices(List.of(price));

      for(String base64Images : request.getImages()){
        String fileName = imageStorageService.store(base64Images);
        ItemImage itemImage = new ItemImage();
        itemImage.setImageName(fileName);
        item.addImage(itemImage);
      }

      return itemRepository.save(item);

    }catch (RuntimeException e) {
      logger.error("Error during creation of the item: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during creation of the item: ", e);
    }
  }

  public ResponseEntity<?> findByCategoryInAndManufacturerIn(List<Long> categoryIds, List<String> manufacturers, Pageable pageable) {
    try{
      Page<Item> items = null;
      if(!manufacturers.isEmpty() && categoryIds.isEmpty()){
        System.out.println("Search by manufactureres");
        items = itemRepository.findByManufacturerIn(manufacturers, pageable);
      }
      if(!categoryIds.isEmpty() && manufacturers.isEmpty()){
        System.out.println("Search by categoryId");
        items = itemRepository.findByCategory_IdIn(categoryIds, pageable);
      }
      if(!categoryIds.isEmpty() && !manufacturers.isEmpty()){
        System.out.println("Search by both");
        items = itemRepository.findByCategory_IdInAndManufacturerIn(categoryIds, manufacturers, pageable);
      }

      if(items == null){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No items were found.");
      }else{
        return ResponseEntity.ok(new PageImpl<>(reduceItemProperies(items), pageable, items.getTotalElements()));
      }
    }catch (RuntimeException e) {
      logger.error("Error when getting items by vendor: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting items by vendor: ", e);
    }
  }

  public ResponseEntity<Page<ReducedItemResponse>> findRandom(Pageable pageable) {
    try{
      Page<Item> items = itemRepository.findRandom(pageable);
      return ResponseEntity.ok(new PageImpl<>(reduceItemProperies(items), pageable, items.getTotalElements()));
    }catch (RuntimeException e) {
      logger.error("Error when getting random items: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting random items: ", e);
    }
  }

  public ResponseEntity<ItemResponse> findByIdForUser(Long id) {
    try{
      Item item = itemRepository.getReferenceById(id);

      ItemResponse request = ItemResponse.builder()
        .price(item.getLatestPrice())
        .name(item.getName())
        .imgUrl(item.getImages().stream().map((ItemImage::getImageName)).toList())
        .quantityInStock(item.getQuantityInStock())
        .category(item.getCategory().getName())
        .weight(item.getWeight())
        .description(item.getDescription())
        .dimensions(item.getDimensions())
        .manufacturer(item.getManufacturer())
        .id(item.getId())
        .build();

      return ResponseEntity.ok(request);
    }catch(EntityNotFoundException e){
      logger.error("Entity not found: " + e.getMessage());
      throw new NotFoundException("Item not found: " + id);
    }
    catch (RuntimeException e) {
      logger.error("Error when getting item by id: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting item by id: ", e);
    }
  }

  public List<ItemResponse> getItemsByIdsForUser(List<Long> ids){
    try{

    }
    catch (RuntimeException e) {
      logger.error("Error when getting items by ids: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting items by ids: ", e);
    }
    List<Item> items = itemRepository.findAllById(ids);
    return toItemResponseList(items);
  }

  private List<ReducedItemResponse> reduceItemProperies(Page<Item> items){
    return items.getContent().stream().map(item -> {
      ReducedItemResponse rir = new ReducedItemResponse();
      rir.setName(item.getName());
      rir.setItemPrice(item.getLatestPrice());
      rir.setImgUrl(item.getImages().get(0).getImageName());
      rir.setRating(item.getRating());
      rir.setId(item.getId());

      return rir;
    }).toList();
  }

  private List<ItemResponse> toItemResponseList(List<Item> items){
    return items.stream().map(item -> {
      return ItemResponse.builder()
        .id(item.getId())
        .price(item.getLatestPrice())
        .category(item.getCategory().getName())
        .description(item.getDescription())
        .dimensions(item.getDimensions())
        .manufacturer(item.getManufacturer())
        .quantityInStock(item.getQuantityInStock())
        .name(item.getName())
        .imgUrl(item.getImages().stream().map((ItemImage::getImageName)).toList())
        .weight(item.getWeight())
        .build();
    }).toList();
  }

  public void addImageToItem(Long itemId, ItemImage image) {
    Item item = itemRepository.getReferenceById(itemId);
    item.addImage(image);
    itemRepository.save(item);
  }
}
