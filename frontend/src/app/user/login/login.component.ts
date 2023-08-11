import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { LoginRequest } from './login-request';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth-service/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  constructor(private http: HttpClient, private auth: AuthService, private router: Router){
    if(auth.getAccessToken() != null){
      router.navigate([""])
    }
  }

  loginRequest: LoginRequest = {
    email: '',
    password: ''
  };

  onSubmit(){
    this.auth.login(this.loginRequest).subscribe({
      next: (r) => {
        this.router.navigate([""])
      }
    });
  }
}
