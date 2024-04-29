import {HttpService} from "../../../../app/shared/http/service/http.service";
import {HttpClient} from "@angular/common/http";
import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";

describe('HttpService', () => {
  let httpService: HttpService;
  let httpClient: HttpClient

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        HttpService
      ]
    });

    httpService = TestBed.inject(HttpService);
    httpClient = TestBed.inject(HttpClient);
  });

  it('should call httpClient.get with expected parameters', () => {
    const url = '/api/data';
    httpClient.get = jest.fn();

    httpService.get(url);

    expect(httpClient.get).toHaveBeenCalledWith(url, {});
  });

  it('should call httpClient.post with expected parameters', () => {
    const url = '/api/data';
    const data = {key: 'value'};

    httpClient.post = jest.fn();

    httpService.post(url, data);

    expect(httpClient.post).toHaveBeenCalledWith(url, data);
  });

  it('should call httpClient.post with expected parameters including options', () => {
    const url = '/api/data';
    const data = {key: 'value'};
    const options = {headers: {'Content-Type': 'application/json'}};

    httpClient.post = jest.fn();

    httpService.postWithOptions(url, data, options);

    expect(httpClient.post).toHaveBeenCalledWith(url, data, options);
  });

  it('should call httpClient.put with expected parameters', () => {
    const url = '/api/data';
    const data = {key: 'value'};

    httpClient.put = jest.fn();

    httpService.put(url, data);

    expect(httpClient.put).toHaveBeenCalledWith(url, data, {});
  });

  it('should call httpClient.delete with expected parameters', () => {
    const url = '/api/data';
    httpClient.delete = jest.fn();

    httpService.delete(url);

    expect(httpClient.delete).toHaveBeenCalledWith(url, {});
  });
});
