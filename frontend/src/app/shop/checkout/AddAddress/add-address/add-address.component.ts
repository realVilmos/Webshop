import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Address } from 'src/app/services/auth-service/user-details';

@Component({
  selector: 'app-add-address',
  templateUrl: './add-address.component.html',
  styleUrls: ['./add-address.component.scss']
})
export class AddAddressComponent {
  shippingAddressForm : FormGroup;
  
  constructor(public dialogRef: MatDialogRef<AddAddressComponent>, @Inject(MAT_DIALOG_DATA) public data: Address, private fb: FormBuilder) { 
    this.shippingAddressForm = this.fb.group({
      street: ['', Validators.required],
      city: ['', Validators.required],
      county: ['', Validators.required],
      postalCode: ['', Validators.required],
      phoneNumber: ['', Validators.required]
    });
  }

  onNoClick(): void{
    this.dialogRef.close();
  }

  onYesClick(): void {
    if (this.shippingAddressForm.valid) {
      this.dialogRef.close(this.shippingAddressForm.value);
    } else {
      console.log('Invalid form');
    }
  }
}
