package hu.vilmosdev.Webshop.Item;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
public class ItemCreationRequest {
  private String name;
  private String description;
  private String category;
  private String manufacturer;
  private String imgUrl;
  private float weight;
  private String dimensions;
  private int quantityInStock;
  private Long vendor;

  private Long originalPrice;
  private boolean isOnSale;
  private Long salePrice;
  private LocalDate saleEndDate;
}
