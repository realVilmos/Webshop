import { Injectable } from '@angular/core';
import { Item } from '../shop/item.model';
import { BehaviorSubject } from 'rxjs';
import { ItemService } from './item/item.service';
import { ReducedItemModel } from '../shop/reduced-item-model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private itemQuantities: BehaviorSubject<Map<number, number>> = new BehaviorSubject<Map<number, number>>(new Map());
  private items: BehaviorSubject<Item[]> = new BehaviorSubject<Item[]>([]);
  constructor(private itemService: ItemService) { 
    this.loadItemsFromLocalStorage();
  }

  public clearCart(): void{
    localStorage.removeItem('itemQuantities');
    this.itemQuantities.next(new Map());
    this.items.next([]);
  }

  public FormatPrice(num: number) : string{
    const reversedStr = num.toString().split('').reverse().join('');
    let formattedStr = reversedStr.replace(/(.{3})/g, "$1 ").trim().split('').reverse().join('');
    return formattedStr
  }

  addItemToCart(item: Item, num: number = 1) : void{
    let newItems = new Map(this.itemQuantities.getValue());
    let items = this.items.getValue();

    const numberOfThisItemInCart = newItems.get(item.id);
    if(numberOfThisItemInCart){
      newItems.set(item.id, num + numberOfThisItemInCart);
    }else{
      newItems.set(item.id, num);
      items.push(item);
    } 

    this.itemQuantities.next(newItems);
    this.items.next(items);
    this.saveItemsToLocalStorage();
  }

  addReducedItemToCart(reducedItem: ReducedItemModel) : void{
    let newItems = new Map(this.itemQuantities.getValue());
    let items = this.items.getValue();

    let item = this.itemService.getItemById(reducedItem.id).subscribe({
      next: (r) => {
        const numberOfThisItemInCart = newItems.get(reducedItem.id);
        if(numberOfThisItemInCart){
          newItems.set(reducedItem.id, 1 + numberOfThisItemInCart);
        }else{
          newItems.set(reducedItem.id, 1);
          items.push(r);
        } 

        this.itemQuantities.next(newItems);
        this.items.next(items);
        this.saveItemsToLocalStorage();
      }
    });
  }

  private saveItemsToLocalStorage(){
    let itemQuantitiesArray = Array.from(this.itemQuantities.getValue().entries());
    localStorage.setItem('itemQuantities', JSON.stringify(itemQuantitiesArray));
  }

  private loadItemsFromLocalStorage(){
    let itemQuantitiesString = localStorage.getItem('itemQuantities');
    if(!itemQuantitiesString){
      return
    }

    let itemQuantitiesArray = JSON.parse(itemQuantitiesString);

    if(Array.isArray(itemQuantitiesArray)){
      this.itemQuantities.next(new Map(itemQuantitiesArray));
    }

    let keys = Array.from(this.itemQuantities.getValue().keys());
    console.log(keys)
    this.itemService.getItemsByIds(keys).subscribe({
      next: (r) => {
        this.items.next(r);
      },
      error: (e) => console.log(e)
    });
  }

  public getItems(): BehaviorSubject<Item[]>{
    return this.items;
  }

  public getitemQuantities(): BehaviorSubject<Map<number, number>>{
    return this.itemQuantities
  }

  public getTotalCost(): number{
    let sum = 0;
    this.items.getValue().forEach((value) => {
      let quantity = this.itemQuantities.getValue().get(value.id)
      if(quantity == undefined){
        return
      }
      if(value.price.onSale){
        this.itemQuantities.getValue().get(value.id)
        sum += quantity * value.price.salePrice
      }else{
        sum += quantity * value.price.originalPrice
      }
    })
    return sum;
  }

  public incrementItemQty(item: Item){
    const itemQuantities = this.itemQuantities.getValue()
    const qty = itemQuantities.get(item.id);
    if(qty) itemQuantities.set(item.id, qty+1)
    this.itemQuantities.next(itemQuantities);
    this.saveItemsToLocalStorage();
  }

  public decreaseItemQty(item: Item){
    const itemQuantities = this.itemQuantities.getValue()
    const qty = itemQuantities.get(item.id);
    if(qty && qty > 1) itemQuantities.set(item.id, qty-1)
    if(qty && qty == 1){
      itemQuantities.delete(item.id);
      let items = this.items.getValue();
      items = items.filter((filter) => filter.id != item.id);
      this.items.next(items);
    } 
    this.itemQuantities.next(itemQuantities);
    this.saveItemsToLocalStorage();
  }

  deleteItem(item: Item){
    const itemQuantities = this.itemQuantities.getValue()
    itemQuantities.delete(item.id);
    let items = this.items.getValue();
    items = items.filter((filter) => filter.id != item.id);
    this.items.next(items);
    this.itemQuantities.next(itemQuantities);
    this.saveItemsToLocalStorage();
  }
}
