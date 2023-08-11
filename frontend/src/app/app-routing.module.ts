import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './user/login/login.component';
import { DashboardComponent } from './dashboard/dashboard/dashboard.component';
import { authGuard } from './guards/auth-guard';
import { RegisterComponent } from './user/register/register.component';
import { VerifyComponent } from './user/verify/verify.component';
import { ShopComponent } from './shop/shop.component';
import { ItemDetailComponent } from './shop/item-detail/item-detail/item-detail.component';
import { CheckoutComponent } from './shop/checkout/checkout/checkout.component';
import { CartComponent } from './shop/cart/cart/cart.component';
import { OrderStatusComponent } from './shop/checkout/order-status/order-status/order-status.component';
import { SearchComponent } from './shop/search/search.component';
import { ItemsComponent } from './shop/items/items.component';

const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'verify',
    component: VerifyComponent
  },
  {
    path: '',
    component: ShopComponent
  },
  {
    path: 'item/:id',
    component: ItemDetailComponent
  },
  {
    path:'order-status',
    component: OrderStatusComponent
  },
  {
    path: 'checkout',
    component: CheckoutComponent
  },
  {
    path: 'cart',
    component: CartComponent
  },
  {
    path: 'items',
    component: ItemsComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
