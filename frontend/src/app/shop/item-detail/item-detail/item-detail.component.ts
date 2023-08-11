import { Component } from '@angular/core';
import { Item } from '../../item.model';
import { ActivatedRoute } from '@angular/router';
import { ItemService } from 'src/app/services/item/item.service';
import { CartService } from 'src/app/services/cart.service';
import { MatTableDataSource } from '@angular/material/table';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-item-detail',
  templateUrl: './item-detail.component.html',
  styleUrls: ['./item-detail.component.scss']
})
export class ItemDetailComponent {
  item: Item | undefined;
  showcaseImg: String = "";
  dataSource: MatTableDataSource<any> = new MatTableDataSource();
  columnMapping: any = {
    'manufacturer': 'Gyártó',
    'category': 'Kategória',
    'quantityInStock': 'Raktáron (db)',
    'weight': 'Súly (g)',
    'dimensions': 'Méretek'
  }
  displayedColumns: string[] = ['attribute', 'value'];

  apiUrl = environment.baseURL;

  constructor(
    private itemService: ItemService,
    private route: ActivatedRoute,
    public cartService: CartService
  ) { 
    const id = this.route.snapshot.paramMap.get('id');
    if(id != null){
      const numericId = +id;
      this.itemService.getItemById(numericId).subscribe(item => {
        this.item = item;
        this.showcaseImg = item.imgUrl[0];
        const attributesArray = Object.entries(this.item)
          .filter(([attribute, _]) => this.columnMapping[attribute])
          .map(([attribute, value]) => ({
            attribute: this.columnMapping[attribute] || attribute, value
        }));

        this.dataSource = new MatTableDataSource(attributesArray);
      }
    );
    }
  }

  addToCart(){
    if(this.item){
      this.cartService.addItemToCart(this.item);
    }
  }

  setShowcaseImage(img: string){
    this.showcaseImg = img;
  }
}
