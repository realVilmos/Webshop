package hu.vilmosdev.Webshop.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private Item item;

  @Override
  public String toString() {
    return "ItemImage{id=" + id + ", imageName=" + imageName + "}";
  }
}
