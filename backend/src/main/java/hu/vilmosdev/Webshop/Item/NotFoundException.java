package hu.vilmosdev.Webshop.Item;

public class NotFoundException extends RuntimeException{
  NotFoundException(String message) {
    super(message);
  }
}
