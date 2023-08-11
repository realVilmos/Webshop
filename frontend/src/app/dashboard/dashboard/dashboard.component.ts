import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Observable, catchError, switchMap } from 'rxjs';
import { Item } from 'src/app/shop/item.model';
import { AuthService } from 'src/app/services/auth-service/auth.service';
import { UserService } from 'src/app/services/user/user.service';
import { UserDetails } from 'src/app/services/auth-service/user-details';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent {
  user: UserDetails | undefined;

  constructor(private http: HttpClient, private auth: AuthService, private userService: UserService){
    userService.getUserDetails().subscribe((user) => this.user = user);
  }

  isAdmin(): boolean{
    return this.auth.isAdmin();
  }



}
