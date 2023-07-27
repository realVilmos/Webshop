package hu.vilmosdev.Webshop.ShopItem.Reviews;

import hu.vilmosdev.Webshop.ShopItem.Item;
import hu.vilmosdev.Webshop.user.User;
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
public class ItemReview {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private int rating;
  private String comment;
  private LocalDate reviewDate;
  @JoinColumn(name = "user_id")
  private User user;

  @OneToOne
  @JoinColumn(name = "item_id")
  private Item item;
}
