package hu.vilmosdev.Webshop.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InvalidUserCredentialsException extends RuntimeException{
  private String message;
}
