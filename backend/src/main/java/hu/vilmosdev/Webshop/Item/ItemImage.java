package hu.vilmosdev.Webshop.Item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ItemImage {

  public ItemImage(String imageName){
    this.imageName = imageName;
  }

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
