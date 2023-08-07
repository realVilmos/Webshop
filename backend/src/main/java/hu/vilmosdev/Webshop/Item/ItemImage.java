package hu.vilmosdev.Webshop.Item;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ItemImage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String imageName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "item_id")
  private Item item;
}
