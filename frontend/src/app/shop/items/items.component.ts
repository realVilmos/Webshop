import { Component, HostListener } from '@angular/core';
import { ReducedItemModel } from '../reduced-item-model';
import { ItemService } from 'src/app/services/item/item.service';
import { CategoryService } from 'src/app/services/category/category.service';
import { Page } from '../page-model';
import { CartService } from 'src/app/services/cart.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.scss']
})
export class ItemsComponent {
  items: ReducedItemModel[] = [];
  page: number = 0;
  size: number = 12;

  apiUrl = environment.baseURL;

  constructor(private itemService: ItemService, private categoryService: CategoryService, public cartService: CartService) {
    this.getRandomItems();
  }

  @HostListener('window:scroll', ['$event'])
  onScroll(event: any): void {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
      this.page++;
      this.getRandomItems();
    }
  }

  getRandomItems(): void {
    this.itemService.getRandomItems(this.page, this.size).subscribe(
      (response: Page<ReducedItemModel>) => {
        console.log(response)
        this.items = [...this.items, ...response.content];
      }
    );
  }
}
