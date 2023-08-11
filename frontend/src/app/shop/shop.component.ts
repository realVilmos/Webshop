import { Component, HostListener, ViewChild } from '@angular/core';
import { Page } from './page-model';
import { ReducedItemModel } from './reduced-item-model';
import { ItemService } from '../services/item/item.service';
import { Category } from '../admin/category-creator/category-model';
import { CategoryService } from '../services/category/category.service';
import { MatMenuTrigger } from '@angular/material/menu';

@Component({
  selector: 'app-shop',
  templateUrl: './shop.component.html',
  styleUrls: ['./shop.component.scss']
})
export class ShopComponent {
  items: ReducedItemModel[] = [];
  page: number = 0;
  size: number = 12;
  categories: Category[] = [];
  vendors: string[] = [];
  hoveredCategory: Category | undefined;

  constructor(private itemService: ItemService, private categoryService: CategoryService) {
    this.categoryService.getCategories().subscribe({
      next: (r) => this.categories = r,
      error: (e) => new Error(e)
    })
  }

  ngOnInit(): void {
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

  /*

  @ViewChild(MatMenuTrigger) trigger: MatMenuTrigger | undefined;

  mouseEnter() {
    this.trigger?.openMenu();
  }
  
  mouseLeave() {
    this.trigger?.closeMenu();
  }*/
}
