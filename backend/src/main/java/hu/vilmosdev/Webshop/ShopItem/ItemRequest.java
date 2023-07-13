package hu.vilmosdev.Webshop.ShopItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
  private String name;
  private String[] categories;
  private String imgUrl;
  private String description;
}
