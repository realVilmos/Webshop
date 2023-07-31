package hu.vilmosdev.Webshop.Item;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VendorService {
  private final VendorRepository vendorRepository;
  private static final Logger logger = LoggerFactory.getLogger(VendorService.class);
  public void saveVendor(Vendor vendor){
    try{
      vendorRepository.save(vendor);
    }
    catch (Exception e) {
      logger.error("Error during creation of vendor: " + e.getMessage());
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during creation of the item: ", e);
    }

  }

}
