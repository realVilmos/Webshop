package hu.vilmosdev.Webshop.ShopItem;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ItemResponse {
  private String name;
  private String description;
  private String category;
  private String manufacturer;
  private String imgUrl;
  private float weight;
  private String dimensions;
  private int quantityInStock;
  private String vendor;

  private Long originalPrice;
  private boolean isOnSale;
  private Long salePrice;
  private LocalDate saleEndDate;
}
