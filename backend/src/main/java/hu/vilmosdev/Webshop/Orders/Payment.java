package hu.vilmosdev.Webshop.Orders;

import hu.vilmosdev.Webshop.Item.Item;
import hu.vilmosdev.Webshop.user.Address;
import hu.vilmosdev.Webshop.user.BillingAddress;
import hu.vilmosdev.Webshop.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
  private String paymentType;
  private boolean isSuccess;
  private String transactionId;

  @ManyToOne
  private BillingAddress billingAddress;
  private LocalDate date;
  private boolean isPaid;
  private double totalPrice;
  @OneToMany
  private List<Item> items;

  @ManyToOne
  private Address deliverAddress;

}
