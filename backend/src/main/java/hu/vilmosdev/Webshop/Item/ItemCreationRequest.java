package hu.vilmosdev.Webshop.Item;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class ItemCreationRequest {
  private String name;
  private String description;
  private Long category;
  private String manufacturer;
  private float weight;
  private String dimensions;
  private int quantityInStock;
  private Long vendor;

  private Long originalPrice;
  private boolean isOnSale;
  private Long salePrice;
  private LocalDate saleEndDate;
  private List<String> images;
}
