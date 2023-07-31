package hu.vilmosdev.Webshop.Item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemPrice {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private Long originalPrice;
  private Long salePrice;
  private boolean isOnSale;
  private LocalDate saleEndDate;
}
