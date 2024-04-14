import {of} from "rxjs";
import {HttpEvent, HttpHandler, HttpRequest} from "@angular/common/http";
import {HttpHeaderInterceptor} from "../../../../app/shared/http/interceptor/http-header.interceptor";

describe('HttpHeaderInterceptor', () => {
  let interceptor: HttpHeaderInterceptor;
  let httpHandlerMock: HttpHandler;

  beforeEach(() => {
    httpHandlerMock = {
      handle: jest.fn(() => of({} as HttpEvent<unknown>))
    } as HttpHandler;
    interceptor = new HttpHeaderInterceptor();
  });

  it('should add withCredentials to the request', () => {
    const request = new HttpRequest('GET', '/api/data');
    interceptor.intercept(request, httpHandlerMock).subscribe(() => {
      expect(httpHandlerMock.handle).toHaveBeenCalledWith(
        expect.objectContaining({
          withCredentials: true
        })
      );
    });
  });
});
