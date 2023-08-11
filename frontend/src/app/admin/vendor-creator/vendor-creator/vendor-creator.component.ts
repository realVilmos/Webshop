import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Vendor } from './vendor-model';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-vendor-creator',
  templateUrl: './vendor-creator.component.html',
  styleUrls: ['./vendor-creator.component.scss']
})
export class VendorCreatorComponent {
  vendorForm: FormGroup | undefined;

  private apiUrl = environment.baseURL + '/api/admin/create-vendor';

  constructor(private fb: FormBuilder, private http: HttpClient) {
    
  }

  ngOnInit(): void {
    this.vendorForm = this.fb.group({
      legalName: ['', Validators.required],
      email: ['', Validators.required],
      phone: ['', Validators.required],
      website: ['', Validators.required],
      registrationNumber: ['', Validators.required],
      bankDetails: ['', Validators.required]
    });
  }

  createVendor(vendor: Vendor): Observable<any> {
    return this.http.post<any>(this.apiUrl, vendor);
  }

  onSubmit(): void {
    if(this.vendorForm){
      if (this.vendorForm.valid) {
        this.createVendor(this.vendorForm.value).subscribe({
          next: (vendor: Vendor) => {console.log('Item created: ', vendor);},
          error: (e) => console.log(e)
        });
      }
    }
  }
}
