package hu.vilmosdev.Webshop.Item;

import hu.vilmosdev.Webshop.Item.Category.Category;
import hu.vilmosdev.Webshop.Item.Reviews.ItemReview;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
  @OneToOne
  private Category category;
  private String manufacturer;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ItemImage> images;

  private float weight;
  private String dimensions;
  private int quantityInStock;

  @ManyToOne
  private Vendor vendor;

  private double rating;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
  private List<ItemReview> reviews;

  @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
  private List<ItemPrice> itemPrices;

  void addImage(ItemImage itemImage){
    if(this.images == null){
      this.images = new ArrayList<>();
    }
    this.images.add(itemImage);
    itemImage.setItem(this);
  }

  public ItemPrice getLatestPrice(){
    return itemPrices.stream()
      .filter(itemPrice -> !itemPrice.getPriceStartDate().isAfter(LocalDate.now()))
      .max(Comparator.comparing(ItemPrice::getPriceStartDate))
      .orElse(null);
  }
  @PrePersist
  private void prePersist() {
    for(ItemPrice itemPrice : this.itemPrices) {
      itemPrice.setItem(this);
    }
    for(ItemReview review: this.reviews){
      review.setItem(this);
    }
    for(ItemImage image: this.images){
      image.setItem(this);
    }
  }

}
