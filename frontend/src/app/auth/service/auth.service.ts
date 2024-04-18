import {inject, Injectable} from "@angular/core";
import {HttpService} from "../../shared/http/service/http.service";
import {BASE_API_URL} from "../../../config";
import {Observable, tap} from "rxjs";
import {HttpHeaders, HttpResponse} from "@angular/common/http";
import {LoginForm} from "../model/LoginForm";
import {RegistrationForm} from "../model/RegistrationForm";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private httpService = inject(HttpService)
  private notificationService = inject(FrontendNotificationService)
  private rootHttpUrl = BASE_API_URL + 'auth/'

  login(login: LoginForm): Observable<HttpResponse<void>> {
    return this.httpService.postWithOptions<HttpResponse<void>>(this.rootHttpUrl + 'login', login, {
      headers: new HttpHeaders({'Content-Type': 'application/json'}),
      withCredentials: true
    }).pipe(tap(() => {
      this.setLoggedIn();
    }))
  }

  logout() {
    this.httpService.get(this.rootHttpUrl + 'logout').subscribe(() => {
      this.notificationService.successNotification('Uživatel byl odhlášen')
      this.setLogout();
    })
  }

  setLogout() {
    sessionStorage.removeItem('auth')
  }

  register(value: RegistrationForm): Observable<Boolean> {
    return this.httpService.post(this.rootHttpUrl + 'register', value)
  }

  isUserSignedIn() {
    return sessionStorage.getItem('auth') === 'true'
  }

  private setLoggedIn() {
    sessionStorage.setItem('auth', `true`)
  }
}
