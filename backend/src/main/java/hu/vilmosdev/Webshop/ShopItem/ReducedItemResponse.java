package hu.vilmosdev.Webshop.ShopItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReducedItemResponse {
  private Long id;
  private String imgUrl;
  private String name;
  private ItemPrice itemPrice;
  private Double rating;
}
