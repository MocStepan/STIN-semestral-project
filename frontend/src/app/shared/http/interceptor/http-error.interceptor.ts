import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {inject, Injectable} from "@angular/core";
import {catchError, Observable, throwError} from "rxjs";
import {AuthService} from "../../../auth/service/auth.service";
import {NotificationService} from "../../notification/service/notification.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private authService = inject(AuthService)
  private notificationService = inject(NotificationService)

  public intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authService.setLogout()
          this.notificationService.errorNotification("Nemáte přístup k této funkci, přihlašte se prosím")
        }
        return throwError(error)
      })
    )
  }
}
