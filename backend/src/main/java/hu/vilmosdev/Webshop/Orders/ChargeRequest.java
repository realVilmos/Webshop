package hu.vilmosdev.Webshop.Orders;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ChargeRequest {
  private Map<Long, Integer> itemQuantities;
  private Long billingAddressId;
  private Long shippingAddressId;
}
