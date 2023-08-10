package hu.vilmosdev.Webshop.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ItemResponse {
  private Long id;
  private String name;
  private String description;
  private String category;
  private String manufacturer;
  private List<String> imgUrl;
  private float weight;
  private String dimensions;
  private int quantityInStock;
  private Long vendor;

  private ItemPrice price;
}
