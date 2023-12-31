package hu.vilmosdev.Webshop.user;

import hu.vilmosdev.Webshop.Orders.Payment;
import hu.vilmosdev.Webshop.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue
  private Long id;
  private String firstname;
  private String lastname;
  @Column(unique = true)
  private String email;
  private String password;

  @Column(name = "verification_code", length = 64)
  private String verificationCode;

  private boolean enabled;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Token> tokens;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Address> addresses;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<BillingAddress> billingAddresses;

  @OneToMany(mappedBy = "user")
  private List<Payment> payments;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String getPassword(){
    return this.password;
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", firstname='" + firstname + '\'' +
      ", lastname='" + lastname + '\'' +
      ", email='" + email + '\'' +
      ", password='" + password + '\'' +
      ", role=" + role +
      '}';
  }

  public void addBillingAddress(BillingAddress billingAddress){
    if(this.billingAddresses == null){
      this.billingAddresses = new ArrayList<>();
    }
    billingAddress.setUser(this);
    this.billingAddresses.add(billingAddress);
  }
}
