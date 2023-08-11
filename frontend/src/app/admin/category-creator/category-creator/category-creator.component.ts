import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Category } from '../category-model';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-category-creator',
  templateUrl: './category-creator.component.html',
  styleUrls: ['./category-creator.component.scss']
})
export class CategoryCreatorComponent {
  categoryForm: FormGroup | undefined;

  private apiUrl = environment.baseURL + '/api/admin/create-category';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    
  }

  ngOnInit(): void {
    this.categoryForm = this.fb.group({
      name: ['', Validators.required],
      parentId: [''],
    });
  }

  createCategory(category: Category): Observable<any> {
    return this.http.post<any>(this.apiUrl, category);
  }

  onSubmit(): void {
    if(this.categoryForm){
      if (this.categoryForm.valid) {
        this.createCategory(this.categoryForm.value).subscribe({
          next: (vendor: Category) => {console.log('Category created: ', vendor);},
          error: (e) => console.log(e)
        });
      }
    }
  }
}
