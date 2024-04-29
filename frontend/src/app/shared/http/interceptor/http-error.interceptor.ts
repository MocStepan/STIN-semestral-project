import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {inject, Injectable} from "@angular/core";
import {catchError, Observable, throwError} from "rxjs";
import {NotificationService} from "../../notification/service/notification.service";

@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  private notificationService = inject(NotificationService)

  public intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.notificationService.errorNotification("You do not have access to this feature, please login")
        }
        return throwError(error)
      })
    )
  }
}
