package hu.vilmosdev.Webshop.config;
import com.stripe.exception.StripeException;
import hu.vilmosdev.Webshop.Item.NotFoundException;
import hu.vilmosdev.Webshop.user.InvalidUserCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(CustomJwtException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Object> handleJwtException(CustomJwtException  ex) {
    String errorMessage = ex.getMessage();
    return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Object> handleUserException(UsernameNotFoundException  ex) {
    String errorMessage = ex.getMessage();
    return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
  }
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Object> customException(RuntimeException ex){
    String errorMessage = ex.getMessage();
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(StripeException.class)
  public ResponseEntity<String> handleStripeException(StripeException e) {
    Logger.getLogger(e.getMessage());
    return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
    String name = ex.getParameterName();
    return new ResponseEntity<>("parameter '" + name + "' is missing", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidUserCredentialsException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ResponseEntity<Object> handleInvalidCredentials(InvalidUserCredentialsException ex) {
    String errorMessage = ex.getMessage();
    return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(NotFoundException.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> notFound(NotFoundException ex) {
    String errorMessage = ex.getMessage();
    return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
  }
}
