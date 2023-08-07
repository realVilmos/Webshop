package hu.vilmosdev.Webshop.Item.Category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryCreationRequest {
  String name;
  Long parentId;
}
