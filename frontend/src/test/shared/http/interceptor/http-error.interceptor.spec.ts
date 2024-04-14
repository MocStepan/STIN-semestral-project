import {HttpErrorResponse, HttpHandler, HttpRequest} from "@angular/common/http";
import {HttpErrorInterceptor} from "../../../../app/shared/http/interceptor/http-error.interceptor";
import {AuthService} from "../../../../app/auth/service/auth.service";
import {
  FrontendNotificationService
} from "../../../../app/shared/frontend-notification/service/frontend-notification.service";
import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('HttpErrorInterceptor', () => {
  let interceptor: HttpErrorInterceptor;
  let authServiceMock: Partial<AuthService>;
  let notificationServiceMock: Partial<FrontendNotificationService>;
  let httpHandlerMock: HttpHandler;

  beforeEach(() => {
    authServiceMock = {
      setLogout: jest.fn()
    };
    notificationServiceMock = {
      errorNotification: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        HttpErrorInterceptor,
        {provide: AuthService, useValue: authServiceMock},
        {provide: FrontendNotificationService, useValue: notificationServiceMock}
      ]
    });

    interceptor = TestBed.inject(HttpErrorInterceptor);
    httpHandlerMock = TestBed.inject(HttpHandler);
  });

  it('should call setLogout and errorNotification on 401 error', () => {
    const request = new HttpRequest('GET', '/api/data');
    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: () => {
        expect(authServiceMock.setLogout).toHaveBeenCalled();
        expect(notificationServiceMock.errorNotification).toHaveBeenCalledWith("Nemáte přístup k této funkci, přihlašte se prosím");
      }
    });
  });

  it('should return throwError', () => {
    const request = new HttpRequest('GET', '/api/data');
    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(401);
      }
    });
  });
});
