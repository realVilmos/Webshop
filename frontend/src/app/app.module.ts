import { InjectionToken, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ItemCreatorComponent } from './admin/item-creator/item-creator.component';
import { LoginComponent } from './user/login/login.component';
import { DashboardComponent } from './dashboard/dashboard/dashboard.component';
import { AdminComponent } from './admin/admin/admin.component';
import { RegisterComponent } from './user/register/register.component';
import { VerifyComponent } from './user/verify/verify.component'; // Import the configuration file
import { AuthService } from './services/auth-service/auth.service';
import { ShopComponent } from './shop/shop.component';
import { ItemDetailComponent } from './shop/item-detail/item-detail/item-detail.component';
import { TokenInterceptorService } from './services/token/token-interceptor.service';
import { VendorCreatorComponent } from './admin/vendor-creator/vendor-creator/vendor-creator.component';
import { PaymentService } from './services/payment/payment.service';
import { CheckoutComponent } from './shop/checkout/checkout/checkout.component';
import { CartComponent } from './shop/cart/cart/cart.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatStepperModule } from '@angular/material/stepper';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import { AddAddressComponent } from './shop/checkout/AddAddress/add-address/add-address.component';
import { MatDialogModule } from '@angular/material/dialog';
import { AddBillingAddressComponent } from './shop/checkout/AddBillingAddress/add-billing-address/add-billing-address.component';
import { Stripe, loadStripe } from '@stripe/stripe-js';
import { OrderStatusComponent } from './shop/checkout/order-status/order-status/order-status.component';
import { CartService } from './services/cart.service';
import { CategoryCreatorComponent } from './admin/category-creator/category-creator/category-creator.component';
import { SearchComponent } from './shop/search/search.component';
import { ItemsComponent } from './shop/items/items.component';
import { ItemService } from './services/item/item.service';

export const STRIPE_TOKEN = new InjectionToken<Stripe>('Stripe');

@NgModule({
  declarations: [
    AppComponent,
    ItemCreatorComponent,
    LoginComponent,
    DashboardComponent,
    AdminComponent,
    RegisterComponent,
    VerifyComponent,
    ShopComponent,
    ItemDetailComponent,
    VendorCreatorComponent,
    CheckoutComponent,
    CartComponent,
    AddAddressComponent,
    AddBillingAddressComponent,
    OrderStatusComponent,
    CategoryCreatorComponent,
    SearchComponent,
    ItemsComponent
  ],
  imports: [
    MatStepperModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatGridListModule,
    MatCardModule,
    MatToolbarModule,
    MatIconModule,
    MatMenuModule,
    MatListModule,
    MatSidenavModule,
    MatDividerModule,
    MatTableModule,
    BrowserAnimationsModule,
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule
  ],
  providers: [
    AuthService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptorService,
      multi: true
    },
    {
      provide: STRIPE_TOKEN,
      useFactory: () => loadStripe('pk_test_51LL5IlB88r1A0tIWAkFfyc3Ldr7kf5WAsQjiiwUEg6OczP6u9rjaP7nc6II9SeOIqKC2S9PLHvk7SIYdJ8VBu1Lv00xUY0W860'),
    },
    PaymentService,
    CartService,
    ItemService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
