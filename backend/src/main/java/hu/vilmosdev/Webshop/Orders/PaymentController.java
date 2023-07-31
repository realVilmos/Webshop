package hu.vilmosdev.Webshop.Orders;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  @PostMapping(value = "/create-payment-intent")
  public ResponseEntity<PaymentIntentResponse> chargeCard(@RequestBody ChargeRequest chargeRequest) throws StripeException {
    System.out.println(chargeRequest);
    return ResponseEntity.ok().body(paymentService.createPaymentIntent(chargeRequest));
  }
}