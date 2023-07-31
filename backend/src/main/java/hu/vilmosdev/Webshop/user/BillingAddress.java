package hu.vilmosdev.Webshop.user;

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
public class BillingAddress {
  @Id
  @GeneratedValue
  private Long id;
  @OneToOne
  @JoinColumn(name = "address_id")
  private Address address;
  private String companyName;
  private String taxNumber;
}
