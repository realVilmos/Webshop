import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Stripe, StripeElement, StripeElements, PaymentIntentResult, PaymentIntent } from '@stripe/stripe-js';
import { ChargeRequest, PaymentIntentResponse } from './models';
import { Observable, switchMap, tap, throwError } from 'rxjs';
import { STRIPE_TOKEN } from 'src/app/app.module';
import { AuthService } from '../auth-service/auth.service';
import { environment } from 'src/environments/environment';
import { ReducedPaymentDetails } from './ReducedPaymentDetails';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private elements: Promise<StripeElements>;
  private cardNumber?: StripeElement;
  private cardExpiry?: StripeElement;
  private cardCvc?: StripeElement;
  isInitialized: boolean = false;

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
    })
  };

  private apiUrl = environment.baseURL + "/api/payment";
  
  constructor(private http: HttpClient, @Inject(STRIPE_TOKEN) private stripePromise: Promise<any>, private authService: AuthService) {
    this.elements = stripePromise.then(s => s.elements());
  }

  getPaymentByIntent(intent: string){
    return this.http.get<ReducedPaymentDetails>(`${this.apiUrl}/get-payment-reference?intentId=${intent}`)
  }

  initializeCardNumber(): Promise<StripeElement> {
    this.isInitialized = true;
    return this.elements.then(e => {
      this.cardNumber = e.create('cardNumber', {
          style: {
              base: {
                  fontSize: '20px',
                  color: '#32325d',
                  '::placeholder': {
                      color: '#aab7c4'
                  }
              },
              invalid: {
                  color: '#fa755a',
              }
          }
      });
      return this.cardNumber;
    });
  }

  getCardNumber() {
    return this.cardNumber;
  }

  initializeCardExpiry(): Promise<StripeElement> {
    this.isInitialized = true;
    return this.elements.then(e => {
      this.cardExpiry = e.create('cardExpiry', {
          style: {
              base: {
                  fontSize: '20px',
                  color: '#32325d',
                  '::placeholder': {
                      color: '#aab7c4'
                  }
              },
              invalid: {
                  color: '#fa755a',
              }
          }
      });
      return this.cardExpiry;
    });
  }

  getCardExpiry(){
    return this.cardExpiry;
  }

  initializeCardCvc(): Promise<StripeElement> {
    this.isInitialized = true;
    return this.elements.then(e => {
      this.cardCvc = e.create('cardCvc', {
          style: {
              base: {
                  fontSize: '20px',
                  color: '#32325d',
                  '::placeholder': {
                      color: '#aab7c4'
                  }
              },
              invalid: {
                  color: '#fa755a',
              }
          }
      });
      return this.cardCvc;
    });
  }

  getCardCvc(){
    return this.cardCvc;
  }

  createPaymentIntent(chargeRequest: ChargeRequest): Observable<PaymentIntent> {
    let transformedRequest: any = {
      ...chargeRequest,
      itemQuantities: Array.from(chargeRequest.itemQuantities.entries()).reduce((obj: Record<number, number>, [key, value]) => {
        obj[key] = value;
        return obj;
      }, {})
    };

    return this.http.post<PaymentIntent>(this.apiUrl + "/create-payment-intent", transformedRequest, this.httpOptions)
      .pipe(
        switchMap((paymentIntent: PaymentIntent) => {
          return this.stripePromise.then(s => {
            const card = this.cardNumber;
            if (card) {
              return s.confirmCardPayment(paymentIntent.client_secret, {
                payment_method: {
                  card: card,
                  billing_details: {
                    name: this.authService.getUser()?.user_id,
                    email: this.authService.getUser()?.email,
                  },
                },
              }).then((result: PaymentIntentResult) => {
                if (result.error) {
                  throw result.error.message;
                } else if (result.paymentIntent && result.paymentIntent.status === 'succeeded') {
                  return result.paymentIntent;
                } else {
                  throw new Error('Payment failed');
                }
              });
            } else {
              throw new Error('Card is empty');
            }
          });
        })
      );
  }
}
