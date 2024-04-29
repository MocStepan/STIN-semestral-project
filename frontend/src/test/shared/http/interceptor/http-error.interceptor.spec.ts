import {HttpErrorInterceptor} from "../../../../app/shared/http/interceptor/http-error.interceptor";
import {NotificationService} from "../../../../app/shared/notification/service/notification.service";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpRequest} from "@angular/common/http";
import {TestBed} from "@angular/core/testing";
import {Observable} from "rxjs";

describe('HttpErrorInterceptor', () => {
  let interceptor: HttpErrorInterceptor;
  let httpHandlerMock: HttpHandler;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HttpErrorInterceptor
      ]
    });

    interceptor = TestBed.inject(HttpErrorInterceptor);
    notificationService = TestBed.inject(NotificationService);
    httpHandlerMock = {
      handle: () => {
        return new Observable<HttpEvent<any>>();
      }
    };
  });

  it('should be created', () => {
    expect(interceptor).toBeTruthy();
  });

  // TODO: Add more tests
  it('should call setLogout and errorNotification on 401 error and rethrow error', () => {
    const request = new HttpRequest('GET', '/api/data');

    notificationService.errorNotification = jest.fn();

    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: (error: HttpErrorResponse) => {
        expect(notificationService.errorNotification).toHaveBeenCalledWith("Nemáte přístup k této funkci, přihlašte se prosím");
        expect(error.status).toBe(401);
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
