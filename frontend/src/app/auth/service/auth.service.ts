import {inject, Injectable} from "@angular/core";
import {HttpService} from "../../shared/http/service/http.service";
import {BASE_API_URL} from "../../../config";
import {Observable, tap} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";
import {SignInForm} from "../model/SignInForm";
import {SignUpForm} from "../model/SignUpForm";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private httpService = inject(HttpService)
  private rootHttpUrl = BASE_API_URL + 'auth/'

  signIn(signInForm: SignInForm): Observable<HttpResponse<void>> {
    return this.httpService.postWithOptions<HttpResponse<void>>(this.rootHttpUrl + 'signIn', signInForm, {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    }).pipe(tap(() => {
      this.setSignedIn();
    }))
  }

  signUp(signUpForm: SignUpForm): Observable<Boolean> {
    return this.httpService.post(this.rootHttpUrl + 'signUp', signUpForm)
  }

  isSignedIn() {
    return sessionStorage.getItem('auth') === 'true'
  }

  private setSignedIn() {
    sessionStorage.setItem('auth', `true`)
  }
}
