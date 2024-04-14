import {inject, Injectable} from "@angular/core";
import {HttpService} from "../../shared/http/service/http.service";
import {API_URL} from "../../../config";
import {Observable, tap} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";
import {LoginForm} from "../model/LoginForm";
import {RegistrationForm} from "../model/RegistrationForm";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private httpService = inject(HttpService)

  login(login: LoginForm): Observable<HttpResponse<void>> {
    return this.httpService.postWithOptions<HttpResponse<void>>(API_URL + '/auth/login', login, {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    }).pipe(tap(() => {
      this.setLoggedIn();
    }))
  }

  logout() {
    this.httpService.get(API_URL + '/auth/logout').subscribe(() => {
      sessionStorage.removeItem('auth')
    })
  }

  private setLoggedIn() {
    sessionStorage.setItem('auth', `true`)
  }

  setLogout() {
    sessionStorage.removeItem('auth')
  }

  register(value: RegistrationForm): Observable<Boolean> {
    return this.httpService.post(API_URL + '/auth/register', value)
  }
}
