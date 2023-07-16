package hu.vilmosdev.Webshop.auth.Exceptions;

public class InsufficientKeyException extends RuntimeException{
  public InsufficientKeyException(String message){
    super(message);
  }
}
