package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.Item.Reviews.ItemReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private String category;
  private String manufacturer;
  private String imgUrl;
  private float weight;
  private String dimensions;
  private int quantityInStock;

  @ManyToOne
  private Vendor vendor;

  private Double rating;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
  private List<ItemReview> reviews;

  @OneToOne(cascade = CascadeType.ALL)
  private ItemPrice itemPrice;

}
