import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { ItemService } from 'src/app/services/item/item.service';
import { Item } from 'src/app/shop/item.model';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-item-creator',
  templateUrl: './item-creator.component.html',
  styleUrls: ['./item-creator.component.scss']
})
export class ItemCreatorComponent {
  itemForm: FormGroup | undefined;
  selectedFiles: File[] = [];

  private apiUrl = environment.baseURL + '/api/admin/create-item';

  constructor(private fb: FormBuilder, private itemService: ItemService, private http: HttpClient) {
    
  }

  ngOnInit(): void {
    this.itemForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      category: ['', Validators.required],
      vendor: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      quantityInStock: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      weight: ['', Validators.required],
      dimensions: ['', Validators.required],
      manufacturer: ['', Validators.required],
      originalPrice: ['', [Validators.required, Validators.pattern("^[0-9]*$")]],
      isOnSale: [false],
      salePrice: [''],
      saleEndDate: [''],
      images: this.fb.array([])
    });
  }

  createItem(item: Item): Observable<any> {
    return this.http.post<any>(this.apiUrl, item);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files) {
      const filesArray = Array.from(input.files);
      filesArray.forEach((file: File) => {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          const fileAsBase64 = e.target.result as string;
          this.imagesFormArray.push(this.fb.control(fileAsBase64));
        };
        reader.readAsDataURL(file);
      });
    }
  }

  get imagesFormArray(): FormArray {
    return this.itemForm?.get('images') as FormArray;
  }

  onSubmit(): void {
    if (this.itemForm && this.itemForm.valid) {
      this.http.post<any>(this.apiUrl, this.itemForm.value).subscribe({
        next: (item: any) => { console.log('Item created: ', item); },
        error: (e) => console.log(e)
      });
    }
  }
}
