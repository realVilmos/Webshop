import { Component } from '@angular/core';
import { CartService } from './services/cart.service';
import { Item } from './shop/item.model';
import { AuthService } from './services/auth-service/auth.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  apiUrl = environment.baseURL;

  totalItemCount = 0;
  items: Item[] = [];
  itemQuantities: Map<number, number> = new Map();
  constructor(public cartService: CartService, public authService: AuthService){
    this.cartService.getitemQuantities().subscribe(itemQuantities => {
      this.totalItemCount = 0;
      this.itemQuantities = itemQuantities;
      console.log(itemQuantities)
      itemQuantities.forEach((quantity : number) => {
        this.totalItemCount += quantity;
      })
    })
    this.cartService.getItems().subscribe(
      items => this.items = items
    )
  }


  getItemQuantity(itemId: number): number {
    return this.itemQuantities?.get(itemId) || 0;
  }
}
