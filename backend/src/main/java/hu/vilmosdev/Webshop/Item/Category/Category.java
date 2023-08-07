package hu.vilmosdev.Webshop.Item.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Category{

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Category category = (Category) o;
    return Objects.equals(id, category.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "parent_id")
  @JsonIgnore
  private Category parent;

  @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
  private Set<Category> subCategories = new HashSet<>();

  @Override
  public String toString() {
    return "Category{id=" + id + ", name=" + name + ", subCategories=" + subCategories + "}";
  }
}
