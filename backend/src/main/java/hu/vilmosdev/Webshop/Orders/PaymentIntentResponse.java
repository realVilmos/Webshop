package hu.vilmosdev.Webshop.Orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {
  private String client_secret;
  private long amount;
  private String currency;
  private String status;
  private String id;
}
