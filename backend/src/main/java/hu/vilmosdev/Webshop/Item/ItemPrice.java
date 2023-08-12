package hu.vilmosdev.Webshop.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  @Column(nullable = false)
  private LocalDate priceStartDate; // When the price became active
  private LocalDate saleEndDate;
  @ManyToOne
  @JoinColumn(name = "item_id")
  @JsonIgnore
  private Item item;

  @PrePersist
  void setPriceStart(){
    if (this.priceStartDate == null) {
      this.priceStartDate = LocalDate.now();
    }
  }

}
