package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.Item.Category.Category;
import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemResponse {
  private Long id;
  private String name;
  private String description;
  private String category;
  private String manufacturer;
  private String imgUrl;
  private float weight;
  private String dimensions;
  private int quantityInStock;
  private Long vendor;

  private ItemPrice price;
}
