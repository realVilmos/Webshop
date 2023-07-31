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
public class Address {
  @Id
  @GeneratedValue
  private Long id;
  private String county;
  private String city;
  private Integer postalCode;
  private String street;
  private String phoneNumber;
}
