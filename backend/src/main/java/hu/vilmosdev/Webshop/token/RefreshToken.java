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
  protected Integer id;

  @Column(unique = true)
  protected String token;

  protected boolean revoked;

  protected boolean expired;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  protected User user;

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
