import { Component } from '@angular/core';
import { Item } from '../../item.model';
import { CartService } from 'src/app/services/cart.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent {
  itemQuantities: Map<number, number> = new Map();
  items: Item[] = [];

  constructor(private cartService: CartService){}

  ngOnInit(){
    this.cartService.getitemQuantities().subscribe(
      itemQuantities => this.itemQuantities = itemQuantities
    )
    this.cartService.getItems().subscribe(
      items => this.items = items
    )
  }
}
