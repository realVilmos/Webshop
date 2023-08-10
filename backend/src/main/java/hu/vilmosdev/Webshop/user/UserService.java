package hu.vilmosdev.Webshop.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AddressRepository addressRepository;
  private final BillingAddressRepoistory billingAddressRepository;
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  @Transactional
  public List<Address> getUserAddresses() {
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken){
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());

        return user.getAddresses();
      }
      throw new Exception("Not valid authorization");
    }
    catch (Exception e) {
      logger.error("Error during getting user addresses: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during getting user addresses: ", e);
    }
  }
  @Transactional
  public ResponseEntity<Address> saveUserAddress(Address address){
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken) {
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());
        address.setUser(user);
        address = addressRepository.save(address);

        return ResponseEntity.ok().body(address);
      }
      throw new Exception("Not valid authorization");
    }
    catch (Exception e) {
      logger.error("Error during saving user addresses: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during saving user addresses: ", e);
    }
  }

  public ResponseEntity<BillingAddress> saveBillingAddress(BillingAddressRequest billingAddressResponse) {
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken) {
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());

        Address address = Address.builder()
          .county(billingAddressResponse.getCounty())
          .city(billingAddressResponse.getCity())
          .street(billingAddressResponse.getStreet())
          .postalCode(billingAddressResponse.getPostalCode())
          .phoneNumber(billingAddressResponse.getPhoneNumber())
          .user(user)
          .build();

        address = addressRepository.save(address);

        BillingAddress billingAddress = BillingAddress.builder()
          .user(user)
          .address(address)
          .companyName(billingAddressResponse.getCompanyName())
          .taxNumber(billingAddressResponse.getTaxNumber())
          .build();

        billingAddress = billingAddressRepository.save(billingAddress);

        return ResponseEntity.ok().body(billingAddress);
      }
      throw new Exception("Not valid authorization");
    }
    catch (Exception e) {
      logger.error("Error during saving of user's billing address: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during saving of user's billing address: ", e);
    }
  }

  public List<BillingAddress> getUserBillingAddresses() {
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken){
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());

        return user.getBillingAddresses();
      }
      throw new Exception("Not valid authorization");
    }catch (Exception e) {
      logger.error("Error when getting user's billing addresses: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting user's billing addresses: ", e);
    }
  }

  public ResponseEntity<UserDetailsResponse> getUserDetails(){
    try{
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if(auth instanceof UsernamePasswordAuthenticationToken){
        User userDetails = (User) auth.getPrincipal();
        User user = userRepository.getReferenceById(userDetails.getId());

        UserDetailsResponse response = UserDetailsResponse.builder()
          .id(user.getId())
          .email(user.getEmail())
          .role(user.getRole().toString())
          .billingAddresses(user.getBillingAddresses())
          .addresses(user.getAddresses())
          .lastname(user.getLastname())
          .firstname(user.getFirstname())
          .build();

        return ResponseEntity.ok(response);
      }
      throw new Exception("Not valid authorization");
    }
    catch (Exception e) {
      logger.error("Error when getting user's details: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error when getting user's billing addresses: ", e);
    }
  }


}
