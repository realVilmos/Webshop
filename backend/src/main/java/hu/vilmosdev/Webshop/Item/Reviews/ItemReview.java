package hu.vilmosdev.Webshop.Item.Reviews;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hu.vilmosdev.Webshop.Item.Item;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

  @ManyToOne
  @JoinColumn(name = "item_id")
  @JsonIgnore
  private Item item;
}
