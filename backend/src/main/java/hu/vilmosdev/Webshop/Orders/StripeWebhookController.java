package hu.vilmosdev.Webshop.Orders;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payment/stripe-webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

  private final PaymentRepository paymentRepository;

  @Value("${stripe.key}")
  private String api_key;

  @Value("${stripe.endpoint}")
  private String endpoint_secret;
  private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

  @PostMapping
  public ResponseEntity handleStripeWebhook(@RequestBody String payload, HttpServletRequest request){
    try{
      String sigHeader = request.getHeader("Stripe-Signature");
      Stripe.apiKey = api_key;
      Event event = null;

      try{
        event = Webhook.constructEvent(payload, sigHeader, endpoint_secret);
      }catch (SignatureVerificationException e){
        e.printStackTrace();
        logger.error("Stripe signature exception: " + e.getMessage());
        return ResponseEntity.badRequest().body("Invalid signature.");
      }

      EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
      StripeObject stripeObject = null;
      if (dataObjectDeserializer.getObject().isPresent()) {
        stripeObject = dataObjectDeserializer.getObject().get();
      } else {
        return ResponseEntity.badRequest().body("Bad stripe version");
      }

      if(stripeObject instanceof PaymentIntent paymentIntent){
        String id = paymentIntent.getId();
        Payment payment = paymentRepository.findByProviderPaymentId(id);

        switch (event.getType()) {
          case "payment_intent.succeeded" -> {
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentRepository.save(payment);
          }
          case "payment_intent.failed" -> {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage(paymentIntent.getLastPaymentError().getMessage());
            paymentRepository.save(payment);
          }
          default -> {
            return ResponseEntity.badRequest().build();
          }
        }
      }
      return ResponseEntity.ok().build();
    }catch (RuntimeException e) {
      logger.error("Stripe webhook exception: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stripe exception", e);
    }


  }

}
