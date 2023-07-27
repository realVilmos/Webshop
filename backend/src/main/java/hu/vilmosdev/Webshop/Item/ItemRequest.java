package hu.vilmosdev.Webshop.ShopItem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ItemRequest {
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
