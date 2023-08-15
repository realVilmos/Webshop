package hu.vilmosdev.Webshop.user;

import hu.vilmosdev.Webshop.Orders.Payment;
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
  private List<Address> addresses;
  private List<BillingAddress> billingAddresses;
  private List<Payment> payments;
}
