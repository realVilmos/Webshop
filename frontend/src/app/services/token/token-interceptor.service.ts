import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, Observable, catchError, filter, finalize, map, switchMap, take, throwError } from 'rxjs';
import { AuthService } from '../auth-service/auth.service';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor{
  constructor(private authService: AuthService, private router: Router) {}
  private apiUrl = environment.baseURL + "/api";

  private refreshingInProgress: boolean = false;
  private tokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const bypassUrls = [this.apiUrl + '/auth', this.apiUrl + '/items']
    //Manage non authenticated requests
    if (bypassUrls.some(url => request.url.startsWith(url))) {
      return next.handle(request).pipe(
        catchError((error: HttpErrorResponse) => {
          if(error.status === 0){
            this.router.navigate(['error', {
              error: "Host not running"
            }])
            return EMPTY;
          }else{
            return throwError(error)
          }
        })
      );
    }


    //manage requests that require autheintication
    return next.handle(this.addAuthenticationToken(request)).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 401) {
          return this.handle401Error(request, next);
        } else {
          return throwError(error);
        }
      })
    );
  }

  private addAuthenticationToken(request: HttpRequest<any>): HttpRequest<any> {
    const token = this.authService.getAccessToken();
    if (!token) {
      return request;
    }
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.refreshingInProgress) {
      this.refreshingInProgress = true;
      this.tokenSubject.next(null);

      return this.authService.getNewTokens().pipe(
        switchMap((newTokens: any) => {
          if (newTokens && newTokens.access_token) {
            this.tokenSubject.next(newTokens.access_token);
            return next.handle(this.addAuthenticationToken(request));
          }
          // If we don't get a new token, we are in trouble so logout.
          return this.logoutUser();
        }),
        catchError(error => {
          // If there is an exception calling 'refreshToken', bad news so logout.
          return this.logoutUser();
        }),
        finalize(() => {
          this.refreshingInProgress = false;
        })
      );
    } else {
      // While refreshing, buffer other incoming requests until the token has been refreshed.
      return this.tokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => {
          return next.handle(this.addAuthenticationToken(request));
        })
      );
    }
  }

  private logoutUser() {
    this.authService.logout();
    return throwError("");
  }
}
