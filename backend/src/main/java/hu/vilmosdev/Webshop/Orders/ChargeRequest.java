package hu.vilmosdev.Webshop.Orders;

import lombok.Data;

import java.util.Map;

@Data
public class ChargeRequest {
  private Long userId;
  private Map<Long, Integer> itemQuantities;
  private Long billingAddressId;
  private Long shippingAddressId;
}
