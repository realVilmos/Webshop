package hu.vilmosdev.Webshop.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/shipping-addresses")
  public ResponseEntity<List<BillingAddressResponse>> getUserRelatedAdresses(){
    return ResponseEntity.ok().body(userService.getUserAddresses());
  }

  @GetMapping("/billing-addresses")
  public ResponseEntity<List<BillingAddressResponse>> getUserRelatedBillingAddresses(){
    return ResponseEntity.ok().body(userService.getUserBillingAddresses());
  }

  @PostMapping("/add-shipping-address")
  public ResponseEntity<BillingAddressResponse> saveUserRelatedAdresses(@RequestBody Address address){
    return userService.saveUserAddress(address);
  }

  @PostMapping("/add-billing-address")
  public ResponseEntity<BillingAddressResponse> addUserRelatedBillingAddress(@RequestBody BillingAddressRequest address){
    return userService.saveBillingAddress(address);
  }

  @GetMapping("/get-details")
  public ResponseEntity<UserDetailsResponse> getUserDetails(){
    return userService.getUserDetails();
  }
}
