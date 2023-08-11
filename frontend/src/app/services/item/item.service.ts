import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Page } from 'src/app/shop/page-model';
import { Item } from 'src/app/shop/item.model';
import { ReducedItemModel } from 'src/app/shop/reduced-item-model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  private apiUrl = environment.baseURL + "/api/items";

  constructor(private http: HttpClient) { }

  getItemsByCategoryAndVendor(page: number, size: number, categories: string[], vendors: string[]): Observable<Page<ReducedItemModel>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('categories', categories.join(','))
      .set('vendors', vendors.join(','));

    return this.http.get<Page<ReducedItemModel>>(this.apiUrl, {params: params});
  }

  getItemById(id: number): Observable<Item> {
    return this.http.get<Item>(`${this.apiUrl}/${id}`);
  }

  getRandomItems(page: number, size: number): Observable<Page<ReducedItemModel>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
        
    return this.http.get<Page<ReducedItemModel>>(`${this.apiUrl}/random`, {params: params});
  }

  getItemsByIds(ids: number[]): Observable<Item[]>{
    let params = new HttpParams().set('ids', ids.join(','));
    return this.http.get<Item[]>(`${this.apiUrl}/batch`, {params: params});
  }
}