package hu.vilmosdev.Webshop.Orders;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import hu.vilmosdev.Webshop.Item.Item;
import hu.vilmosdev.Webshop.Item.ItemPrice;
import hu.vilmosdev.Webshop.Item.ItemQuantity;
import hu.vilmosdev.Webshop.Item.ItemRepository;
import hu.vilmosdev.Webshop.user.AddressRepository;
import hu.vilmosdev.Webshop.user.BillingAddressRepoistory;
import hu.vilmosdev.Webshop.user.User;
import hu.vilmosdev.Webshop.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
            ItemPrice price = item.getLatestPrice();
            if (chargeRequest.getItemQuantities().get(item.getId()) > 0) {
              if (price.isOnSale() && LocalDate.now().isBefore(price.getSaleEndDate())) {
                return price.getSalePrice() * chargeRequest.getItemQuantities().get(item.getId());
              }else {
                return price.getOriginalPrice() * chargeRequest.getItemQuantities().get(item.getId());
              }
            }else{
              throw new RuntimeException("Invalid order");
            }
          }).sum();

        Stripe.apiKey = apiKey;
        Map<String, Object> params = new HashMap<>();
        params.put("amount", totalAmount*100);
        params.put("currency", "HUF");
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        PaymentIntentResponse paymentIntentResponse = PaymentIntentResponse.builder()
          .client_secret(paymentIntent.getClientSecret())
          .amount(paymentIntent.getAmount())
          .currency(paymentIntent.getCurrency())
          .status(paymentIntent.getStatus())
          .id(paymentIntent.getId())
          .build();

        Payment payment = Payment.builder()
          .user(userRepository.getReferenceById(chargeRequest.getUserId()))
          .billingAddress(billingAddressRepoistory.getReferenceById(chargeRequest.getBillingAddressId()))
          .deliveryAddress(addressRepository.getReferenceById(chargeRequest.getShippingAddressId()))
          .items(items.stream().map(item -> ItemQuantity.builder()
            .quantity(chargeRequest.getItemQuantities().get(item.getId()))
            .item(item)
            .build()).toList())
          .provider("STRIPE")
          .providerPaymentId(paymentIntent.getId())
          .paymentMethod("CARD")
          .status(PaymentStatus.PENDING)
          .currency("HUF")
          .totalPrice(totalAmount)
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

  public ReducedPaymentResponse getPaymentDetails(String intentId) {
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken){
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());

        Payment payment = paymentRepository.findByProviderPaymentId(intentId);

        if(Objects.equals(payment.getUser().getId(), user.getId())){
          return ReducedPaymentResponse.builder()
            .paymentReference(payment.getPaymentReference())
            .paymentMethod(payment.getPaymentMethod())
            .address(payment.getDeliveryAddress())
            .billingAddress(payment.getBillingAddress())
            .createdDate(payment.getCreatedDate())
            .errorMessage(payment.getErrorMessage())
            .totalPrice(payment.getTotalPrice())
            .currency(payment.getCurrency())
            .items(payment.getItems())
            .build();
        }else{
          throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to get other payments.");
        }
      }
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user authentication");

    }catch (RuntimeException e) {
      logger.error("Error during getting user addresses: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during getting user addresses: ", e);
    }
  }
}
