import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http'
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, finalize, map, switchMap, tap, timeout } from 'rxjs/operators';
import { Router } from '@angular/router';
import { UserDetails } from './user-details';
import { RegisterRequest } from '../../user/register/register-request';
import { LoginRequest } from '../../user/login/login-request';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessTokenKey = 'access_token';
  private refreshTokenKey = 'refresh_token';
  private apiUrl = environment.baseURL + '/api/auth';
  private userDetails: UserDetails | null;
  private accessToken$: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  private isRefreshingToken = new BehaviorSubject<boolean>(false);
  public isRefreshingToken$ = this.isRefreshingToken.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    const userDetailsString = localStorage.getItem('userDetails');

    this.userDetails = userDetailsString ? JSON.parse(userDetailsString) : null;

    if(this.userDetails){
      this.accessToken$.next(this.userDetails.access_token)
      this.setRefreshToken(this.userDetails.refresh_token)
    }

  }

  logout() {
    this.accessToken$.next(null);
    this.userDetails = null;
    localStorage.setItem('userDetails', "");
    this.router.navigate(["login"])
  }

  login(loginData: LoginRequest) {
    const url = this.apiUrl + "/authenticate"
    return this.http.post<UserDetails>(url, loginData).pipe(
      map((response: UserDetails) => {
      this.userDetails = response;
      localStorage.setItem('userDetails', JSON.stringify(response));
      this.accessToken$.next(response.access_token);
      return response;
    }))
  }

  register(registerData: RegisterRequest){
    const url = this.apiUrl + "/register";
    return this.http.post<any>(url, registerData)
  }

  getNewTokens(): Observable<any> {
    this.isRefreshingToken.next(true);
    const refreshToken = this.getRefreshToken();

    if(!refreshToken){
      return throwError('User is logged out');
    }

    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + refreshToken
    });

    console.log("refresh token: " + refreshToken)

    console.log(headers)

    const url = this.apiUrl + "/refresh-token"
    console.log(url)

    return this.http.get<any>(url, {headers}).pipe(
      tap(() => console.log('Making request to refresh token...')),
      map((response: any) => {
        console.log(response)
        this.setAccessToken(response.access_token);
        this.setRefreshToken(response.refresh_token);
        return response;
      }),
      catchError((error: any) => {
        console.log("Error trying to refresh the token: ")
        console.log(error)
        this.logout();
        return throwError(error);;
      }),
      finalize(() => this.isRefreshingToken.next(false))
    )
  }

  getAccessToken(): string | null {
    return this.accessToken$.getValue();
  }

  getRefreshToken(): string | null {
    return this.userDetails ? this.userDetails.refresh_token : null;
  }

  setAccessToken(token: string): void{
    this.userDetails ? this.userDetails.access_token = token : null;
    localStorage.setItem('userDetails', JSON.stringify(this.userDetails));
    this.accessToken$.next(token);
  }

  setRefreshToken(token: string): void{
    this.userDetails ? this.userDetails.refresh_token = token : null;
    localStorage.setItem('userDetails', JSON.stringify(this.userDetails));
  }

  isAdmin() : boolean {
    return this.userDetails ? this.userDetails.role == "ADMIN" : false;
  }

  getUser() : UserDetails | null{
    return this.userDetails
  }

  getUserName(){
    const user = this.getUser()
    if(user){
      return user.last_name + " " + user.first_name;
    }
    return "";
  }
  
}
