import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, switchMap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.scss']
})
export class VerifyComponent {
  constructor(private route: ActivatedRoute, private router: Router, private http: HttpClient) { }

  isSuccessful: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  private apiUrl = environment.baseURL + "/api/auth";

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const codeParam = params['code'];
      const url = this.apiUrl + "/verify?code=" + codeParam
      if(codeParam){
        this.http.get<any>(url).
        subscribe({
          next: (r) => {
            console.log(r)
            if(r.message == "verify_success")
              this.isSuccessful.next(true)
            else
              this.isSuccessful.next(false)
          },
          error: (e) => {
            console.log(e)
          }
        })
      }else{
        this.router.navigate([""]);
      }
    })
  }
}
