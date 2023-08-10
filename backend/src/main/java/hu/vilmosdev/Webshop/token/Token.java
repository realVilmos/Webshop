package hu.vilmosdev.Webshop.token;
import hu.vilmosdev.Webshop.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true, columnDefinition="text")
  private String token;

  @OneToOne(cascade = CascadeType.ALL)
  @MapsId
  private RefreshToken relatedTo;

  private boolean revoked;
  private boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Override
  public String toString() {
    return "Token{" +
      "id=" + id +
      ", token='" + token + '\'' +
      ", revoked=" + revoked +
      ", expired=" + expired +
      '}';
  }
}
