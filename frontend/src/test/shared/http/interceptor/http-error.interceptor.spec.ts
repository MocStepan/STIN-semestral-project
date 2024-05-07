import {HttpErrorInterceptor} from "../../../../app/shared/http/interceptor/http-error.interceptor";
import {NotificationService} from "../../../../app/shared/notification/service/notification.service";
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpRequest} from "@angular/common/http";
import {TestBed} from "@angular/core/testing";
import {Observable, throwError} from "rxjs";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {Router} from "@angular/router";

describe('HttpErrorInterceptor', () => {
  let interceptor: HttpErrorInterceptor;
  let httpHandlerMock: HttpHandler;
  let notificationService: NotificationService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        HttpErrorInterceptor
      ]
    });

    interceptor = TestBed.inject(HttpErrorInterceptor);
    notificationService = TestBed.inject(NotificationService);
    router = TestBed.inject(Router);
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
    router.navigate = jest.fn();
    httpHandlerMock.handle = jest.fn().mockReturnValue(throwError(new HttpErrorResponse({ status: 401 })))

    interceptor.intercept(request, httpHandlerMock).subscribe(() =>{
        expect(notificationService.errorNotification).toHaveBeenCalledWith("Nemáte přístup k této funkci, přihlašte se prosím");
        expect(router.navigate).toHaveBeenCalledWith(['/signIn']);
    });
  });

  it('should return throwError', () => {
    const request = new HttpRequest('GET', '/api/data');
    httpHandlerMock.handle = jest.fn().mockReturnValue(throwError(new HttpErrorResponse({ status: 401 })))

    interceptor.intercept(request, httpHandlerMock).subscribe({
      error: (error: HttpErrorResponse) => {
        expect(error.status).toBe(401);
      }
    });
  });
});
