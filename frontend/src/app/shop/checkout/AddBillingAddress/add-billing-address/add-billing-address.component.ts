import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BillingAddress } from 'src/app/services/auth-service/user-details';

@Component({
  selector: 'app-add-billing-address',
  templateUrl: './add-billing-address.component.html',
  styleUrls: ['./add-billing-address.component.scss']
})
export class AddBillingAddressComponent {
  billingAddressForm: FormGroup;

  constructor(public dialogRef: MatDialogRef<AddBillingAddressComponent>, @Inject(MAT_DIALOG_DATA) public data: BillingAddress, private fb: FormBuilder){
    this.billingAddressForm = this.fb.group({
      street: ['', Validators.required],
      city: ['', Validators.required],
      county: ['', Validators.required],
      postalCode: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      companyName: [''],
      taxNumber: ['']
    });
  }

  onNoClick(): void{
    this.dialogRef.close();
  }

  onYesClick(): void {
    if (this.billingAddressForm.valid) {
      this.dialogRef.close(this.billingAddressForm.value);
    } else {
      console.log('Invalid form');
    }
  }
}
