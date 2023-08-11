import { Component, EventEmitter } from '@angular/core';
import { CartService } from 'src/app/services/cart.service';
import { AuthService } from 'src/app/services/auth-service/auth.service';
import { Address, BillingAddress } from 'src/app/services/auth-service/user-details';
import { UserService } from 'src/app/services/user/user.service';
import { Item } from '../../item.model';
import { PaymentService } from 'src/app/services/payment/payment.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AddBillingAddressComponent } from '../AddBillingAddress/add-billing-address/add-billing-address.component';
import { AddAddressComponent } from '../AddAddress/add-address/add-address.component';
import { ChargeRequest } from 'src/app/services/payment/models';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent {
  isLoggedIn: boolean;
  shippingAddresses: Address[] = [];
  billingAddresses: BillingAddress[] = [];
  items: Item[] = [];
  itemQuantities: Map<number, number> = new Map();
  selectedShippingAddress: Address | undefined;
  selectedBillingAddress: BillingAddress | undefined;
  apiUrl = environment.baseURL;
  
  constructor(private authService: AuthService, public cartService: CartService, private userService: UserService, private paymentService: PaymentService, private dialog: MatDialog, private router: Router){
    this.isLoggedIn = this.authService.getAccessToken() ?  true : false
    if(this.isLoggedIn){
      userService.getUserBillingAddresses().subscribe(addresses => {
        this.billingAddresses = addresses
        console.log(this.billingAddresses)
      })

      userService.getUserShippingAddresses().subscribe(addresses => {
        this.shippingAddresses = addresses
        console.log(this.shippingAddresses)
      })
    }

    this.cartService.getItems().subscribe(
      items => this.items = items
    )
    
    this.cartService.getitemQuantities().subscribe(
      itemQuantities => this.itemQuantities = itemQuantities
    )
  }

  ngAfterViewInit(): void {
    if(this.paymentService.isInitialized){
      this.paymentService.getCardNumber()?.mount('#card-number-element');
      this.paymentService.getCardExpiry()?.mount('#card-expiry-element');
      this.paymentService.getCardCvc()?.mount('#card-cvc-element');
    }else{
      this.paymentService.initializeCardNumber().then(cardNumber => {
        cardNumber.mount('#card-number-element');
      });
    
      this.paymentService.initializeCardExpiry().then(cardExpiry => {
          cardExpiry.mount('#card-expiry-element');
      });
      
      this.paymentService.initializeCardCvc().then(cardCvc => {
          cardCvc.mount('#card-cvc-element');
      });
    }
  }

  getItemQuantity(itemId: number): number {
    return this.itemQuantities?.get(itemId) || 0;
  }

  summarizeCost(): number{
    let sum = 0;
    this.items.forEach((value) => {
      let quantity = this.itemQuantities.get(value.id)
      if(quantity == undefined){
        return
      }
      if(value.price.onSale){
        this.itemQuantities.get(value.id)
        sum += quantity * value.price.salePrice
      }else{
        sum += quantity * value.price.originalPrice
      }
    })
    return sum;
  }

  getFullName() : String{
    return this.authService.getUser()?.last_name + " " + this.authService.getUser()?.first_name
  }

  registerBillingAddress(data: BillingAddress){
    this.userService.registerUserBillingAddress(data).subscribe(billingAddress => {
        this.billingAddresses.push(billingAddress)
        this.shippingAddresses.push(billingAddress.address)
      }
    )
  }

  registerShippingAddress(data: Address){
    this.userService.registerUserShippingAddress(data).subscribe(
      address => this.shippingAddresses.push(address)
    )
  }

  checkout(){
    if(this.selectedBillingAddress && this.selectedShippingAddress){
      let user = this.authService.getUser();
      if(user){
        let chargeRequest: ChargeRequest = {
          userId: user.user_id,
          itemQuantities: this.itemQuantities,
          billingAddressId: this.selectedBillingAddress.id,
          shippingAddressId: this.selectedShippingAddress.id
        }
        this.paymentService.createPaymentIntent(chargeRequest).subscribe((paymentIntent) => {
          console.log(paymentIntent)
          if(paymentIntent.status === 'succeeded') {
            this.cartService.clearCart()
            this.router.navigate(['order-status'], {
                queryParams: { 
                  status: 'success',
                  reference: paymentIntent.id
                } 
            });
          } else {
            this.router.navigate(['order-status'], {
              queryParams: { 
                status: 'fail',
                reference: paymentIntent.id
              } 
            });
          }
        },(error) => {
          console.error('Payment failed:', error);
          this.router.navigate(['order-status'], {
            queryParams: { 
              status: 'fail', 
              message: error.message
            } 
          });
        })
      }
    }
  }

  selectBillingAddress(billingAddress: BillingAddress) {
    this.selectedBillingAddress = billingAddress;
  }

  selectShippingAddress(shippingAddress: Address) {
    this.selectedShippingAddress = shippingAddress;
  }
  
  openRegisterBillingAddressModal(){
    const dialogRef = this.dialog.open(AddBillingAddressComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.registerBillingAddress(result)
      } else {
        console.log('Dialog was closed without saving');
      }
    });
  }

  openRegisterAddressModal(){
    const dialogRef = this.dialog.open(AddAddressComponent, {
      width: '500px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.registerShippingAddress(result)
      } else {
        console.log('Dialog was closed without saving');
      }
    });
  }
}
