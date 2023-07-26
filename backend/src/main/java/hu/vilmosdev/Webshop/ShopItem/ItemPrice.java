package hu.vilmosdev.Webshop.ShopItem;

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
  private double originalPrice;
  private double salePrice;
  private boolean isOnSale;
  private LocalDate saleEndDate;
}
