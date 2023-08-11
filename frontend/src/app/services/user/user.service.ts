import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Address, BillingAddress, UserDetails } from '../auth-service/user-details';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  private apiUrl = environment.baseURL + "/api/user";

  getUserShippingAddresses() : Observable<Address[]>{
    return this.http.get<Address[]>(`${this.apiUrl}/shipping-addresses`);
  }

  getUserBillingAddresses() : Observable<BillingAddress[]>{
    return this.http.get<BillingAddress[]>(`${this.apiUrl}/billing-addresses`);
  }

  registerUserShippingAddress(address: Address) : Observable<Address>{
    return this.http.post<Address>(`${this.apiUrl}/add-shipping-address`, address);
  }

  registerUserBillingAddress(address: BillingAddress) : Observable<BillingAddress>{
    return this.http.post<BillingAddress>(`${this.apiUrl}/add-billing-address`, address);
  }

  getUserDetails(): Observable<UserDetails>{
    return this.http.get<UserDetails>(`${this.apiUrl}/get-details`);
  }
}
