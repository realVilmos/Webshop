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
public class RefreshToken {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String token;

  @OneToOne
  @JoinColumn(name = "id")
  private Token relatedTo;

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
