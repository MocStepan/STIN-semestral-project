import {HttpService} from "../../../../app/shared/http/service/http.service";
import {HttpClient} from "@angular/common/http";
import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('HttpService', () => {
  let httpService: HttpService;
  let httpClientMock: Partial<HttpClient>;

  beforeEach(() => {
    httpClientMock = {
      get: jest.fn(),
      post: jest.fn(),
      put: jest.fn(),
      delete: jest.fn()
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        HttpService,
        {provide: HttpClient, useValue: httpClientMock}
      ]
    });

    httpService = TestBed.inject(HttpService);
  });

  it('should call httpClient.get with expected parameters', () => {
    const url = '/api/data';
    httpService.get(url);
    expect(httpClientMock.get).toHaveBeenCalledWith(url, {});
  });

  it('should call httpClient.post with expected parameters', () => {
    const url = '/api/data';
    const data = {key: 'value'};
    httpService.post(url, data);
    expect(httpClientMock.post).toHaveBeenCalledWith(url, data);
  });

  it('should call httpClient.post with expected parameters including options', () => {
    const url = '/api/data';
    const data = {key: 'value'};
    const options = {headers: {'Content-Type': 'application/json'}};
    httpService.postWithOptions(url, data, options);
    expect(httpClientMock.post).toHaveBeenCalledWith(url, data, options);
  });

  it('should call httpClient.put with expected parameters', () => {
    const url = '/api/data';
    const data = {key: 'value'};
    httpService.put(url, data);
    expect(httpClientMock.put).toHaveBeenCalledWith(url, data, {});
  });

  it('should call httpClient.delete with expected parameters', () => {
    const url = '/api/data';
    httpService.delete(url);
    expect(httpClientMock.delete).toHaveBeenCalledWith(url, {});
  });
});
