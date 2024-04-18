import {HttpErrorInterceptor} from "../../../../app/shared/http/interceptor/http-error.interceptor";
import {AuthService} from "../../../../app/auth/service/auth.service";
import {
  FrontendNotificationService
} from "../../../../app/shared/frontend-notification/service/frontend-notification.service";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpRequest} from "@angular/common/http";
import {TestBed} from "@angular/core/testing";
import {Observable} from "rxjs";

describe('HttpErrorInterceptor', () => {
  let interceptor: HttpErrorInterceptor;
  let authServiceMock: Partial<AuthService>;
  let notificationServiceMock: Partial<FrontendNotificationService>;
  let httpHandlerMock: HttpHandler;

  beforeEach(() => {
    authServiceMock = {
      setLogout: () => {
      }
    };
    notificationServiceMock = {
      errorNotification: () => {
      }
    };

    TestBed.configureTestingModule({
      providers: [
        HttpErrorInterceptor,
        {provide: AuthService, useValue: authServiceMock},
        {provide: FrontendNotificationService, useValue: notificationServiceMock}
      ]
    });

    interceptor = TestBed.inject(HttpErrorInterceptor);
    httpHandlerMock = {
      handle: () => {
        return new Observable<HttpEvent<any>>();
      }
    };
  });

  it('should call setLogout and errorNotification on 401 error and rethrow error', () => {
    const request = new HttpRequest('GET', '/api/data');
    const errorResponse = new HttpErrorResponse({status: 401});

    jest.spyOn(authServiceMock, 'setLogout');
    jest.spyOn(notificationServiceMock, 'errorNotification');

    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: (error: HttpErrorResponse) => {
        expect(authServiceMock.setLogout).toHaveBeenCalled();
        expect(notificationServiceMock.errorNotification).toHaveBeenCalledWith("Nemáte přístup k této funkci, přihlašte se prosím");
        expect(error.status).toBe(401);
      }
    });
  });

  it('should return throwError', () => {
    const request = new HttpRequest('GET', '/api/data');
    const errorResponse = new HttpErrorResponse({status: 401});

    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(401);
      }
    });
  });
});
