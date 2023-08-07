package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.Item.Category.Category;
import jakarta.persistence.OneToOne;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
