package hu.vilmosdev.Webshop.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
  @Id
  @GeneratedValue
  Long id;
  String country;
  String county;
  Integer postalCode;
  String street_houseNumber;
}
