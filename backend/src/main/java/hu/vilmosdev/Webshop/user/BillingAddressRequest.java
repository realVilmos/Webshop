package hu.vilmosdev.Webshop.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingAddressRequest {
  private String county;
  private Integer postalCode;
  private String city;
  private String street;
  private String phoneNumber;
  private String companyName;
  private String taxNumber;
}
