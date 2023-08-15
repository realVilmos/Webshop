package hu.vilmosdev.Webshop.Orders;

import hu.vilmosdev.Webshop.Item.ItemQuantity;
import hu.vilmosdev.Webshop.user.Address;
import hu.vilmosdev.Webshop.user.BillingAddress;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReducedPaymentResponse {
  private UUID paymentReference;
  private BillingAddress billingAddress;
  private Address address;
  private List<ItemQuantity> items;
  private String paymentMethod;
  private String errorMessage;
  private LocalDateTime createdDate;
  private double totalPrice;
  private String currency;
  private String status;

}
