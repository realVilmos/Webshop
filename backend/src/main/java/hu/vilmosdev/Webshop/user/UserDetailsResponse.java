package hu.vilmosdev.Webshop.user;

import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDetailsResponse {
  private Long id;
  private String firstname;
  private String lastname;
  private String email;
  private String role;
  @OneToMany(mappedBy = "user")
  private List<Address> addresses;
  @OneToMany(mappedBy = "user")
  private List<BillingAddress> billingAddresses;
}
