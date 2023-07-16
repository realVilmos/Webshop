package hu.vilmosdev.Webshop.auth.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomerExceptionHandlerAPI {
  @ExceptionHandler(InsufficientKeyException.class)
  public ResponseEntity<String> handleInsufficientKeyException(InsufficientKeyException ex){
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}
