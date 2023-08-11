import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterRequest } from './register-request';
import { AuthService } from '../../services/auth-service/auth.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  constructor(private fb: FormBuilder, private http: HttpClient, private auth: AuthService, private router: Router){
    
    if(auth.getAccessToken() != null){
      router.navigate([""])
    }
  }

  registerForm: FormGroup | undefined;

  isRegistered: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  ngOnInit(): void {
    this.registerForm = this.fb.group({
      email: ['', Validators.required],
      password: ['', Validators.required],
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
    });
  }

  onSubmit(){
    this.auth.register(this.registerForm?.value).subscribe({
      next: (r) => {
        this.isRegistered.next(true);
      }
    });
  }
}
