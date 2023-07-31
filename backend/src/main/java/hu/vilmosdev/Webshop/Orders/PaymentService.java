package hu.vilmosdev.Webshop.Orders;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import hu.vilmosdev.Webshop.Item.Item;
import hu.vilmosdev.Webshop.Item.ItemPrice;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.user.AddressRepository;
import hu.vilmosdev.Webshop.user.BillingAddressRepoistory;
import hu.vilmosdev.Webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
  @Value("${stripe.key}")
  private String apiKey;

  private final ItemRepository itemRepository;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final BillingAddressRepoistory billingAddressRepoistory;
  public PaymentIntentResponse createPaymentIntent(ChargeRequest chargeRequest) throws StripeException {
      try{
        List<Item> items = itemRepository.findAllById(chargeRequest.getItemQuantities().keySet());

        long totalAmount = items
          .stream()
          .mapToLong(item -> {
            ItemPrice price = item.getItemPrice();
            if (chargeRequest.getItemQuantities().get(item.getId()) > 0) {
              if (price.isOnSale() && LocalDate.now().isBefore(price.getSaleEndDate())) {
                return price.getSalePrice() * chargeRequest.getItemQuantities().get(item.getId());
              }else {
                return price.getOriginalPrice() * chargeRequest.getItemQuantities().get(item.getId());
              }
            }else{
              System.out.println("Hibás");
              return 0L;
            }
          }).sum();

        System.out.println(totalAmount);

        Stripe.apiKey = apiKey;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", totalAmount*100);
        params.put("currency", "HUF");
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        PaymentIntentResponse paymentIntentResponse = PaymentIntentResponse.builder()
          .clientSecret(paymentIntent.getClientSecret())
          .amount(paymentIntent.getAmount())
          .currency(paymentIntent.getCurrency())
          .status(paymentIntent.getStatus())
          .build();

        Payment payment = Payment.builder()
          .paymentType("STRIPE")
          .billingAddress(billingAddressRepoistory.getReferenceById(chargeRequest.getBillingAddressId()))
          .date(LocalDate.now())
          .deliverAddress(addressRepository.getReferenceById(chargeRequest.getShippingAddressId()))
          .totalPrice(totalAmount)
          .user(userRepository.getReferenceById(chargeRequest.getUserId()))
          .transactionId(paymentIntent.getId())
          .isSuccess(paymentIntent.getStatus().equals("succeeded"))
          .build();

        paymentRepository.save(payment);
        return paymentIntentResponse;
      }catch (StripeException e) {
        logger.error("Stripe exception: " + e.getMessage());
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Stripe exception", e);
      } catch (Exception e) {
        logger.error("General exception: " + e.getMessage());
        e.printStackTrace();
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "General exception", e);
      }
  }
}
