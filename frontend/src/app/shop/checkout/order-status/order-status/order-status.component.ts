import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ReducedPaymentDetails } from 'src/app/services/payment/ReducedPaymentDetails';
import { PaymentService } from 'src/app/services/payment/payment.service';

@Component({
  selector: 'app-order-status',
  templateUrl: './order-status.component.html',
  styleUrls: ['./order-status.component.scss']
})
export class OrderStatusComponent {
  constructor(private route: ActivatedRoute, private paymentService: PaymentService){

  }

  status: String = "";
  errorMessage: String = "";
  payment: ReducedPaymentDetails | undefined;

  ngOnInit(){
    this.route.queryParams.subscribe(params => {
      this.status = params['status'];
      this.errorMessage = params['message'];
      const panmentIntentId = params['reference'];
      if(panmentIntentId){
        this.paymentService.getPaymentByIntent(panmentIntentId).subscribe(
          payment => this.payment = payment
        )
      }
    })
  }
}
