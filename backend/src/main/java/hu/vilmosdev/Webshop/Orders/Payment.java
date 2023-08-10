package hu.vilmosdev.Webshop.Orders;

import hu.vilmosdev.Webshop.Item.ItemQuantity;
import hu.vilmosdev.Webshop.user.Address;
import hu.vilmosdev.Webshop.user.BillingAddress;
import hu.vilmosdev.Webshop.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private UUID paymentReference; //Ez megy ki a felhasználónak

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "billing_id", nullable = false)
  private BillingAddress billingAddress;

  @ManyToOne
  @JoinColumn(name = "address_id", nullable = false)
  private Address deliveryAddress;

  @OneToMany(cascade = CascadeType.ALL)
  private List<ItemQuantity> items;

  @Column(nullable = false)
  private String provider; //Pl: Stripe, Paypal, stb...

  @Column(nullable = false)
  private String providerPaymentId;

  @Column(nullable = false)
  private String paymentMethod; // pl CARD, PAYPAL_BALANCE stb...

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private String errorMessage;

  @Column(nullable = false)
  private LocalDateTime createdDate;

  private LocalDateTime updatedDate;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private double totalPrice;

  @PrePersist
  protected void onCreate() {
    this.createdDate = LocalDateTime.now();
    this.paymentReference = UUID.randomUUID();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedDate = LocalDateTime.now();
  }
}

enum PaymentStatus {
  PENDING,
  SUCCESS,
  FAILED,
  REFUNDED
}
