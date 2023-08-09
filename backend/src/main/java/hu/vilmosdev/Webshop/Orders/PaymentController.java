package hu.vilmosdev.Webshop.Orders;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping(value = "/get-payment-reference")
  public ResponseEntity<ReducedPaymentResponse> getPaymentReference(@RequestParam String intentId){
    System.out.println(ResponseEntity.ok().body(paymentService.getPaymentDetails(intentId)));
    return ResponseEntity.ok().body(paymentService.getPaymentDetails(intentId));
  }
}
