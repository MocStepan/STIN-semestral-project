import {HttpClient} from "@angular/common/http";
import {inject, Injectable} from "@angular/core";

@Injectable({providedIn: 'root'})
export class HttpService {

  constructor(private httpClient: HttpClient) {
  }

  get<T>(url: string, options = {}) {
    return this.httpClient.get<T>(url, options)
  }

  post<T>(url: string, data: unknown) {
    return this.httpClient.post<T>(url, data)
  }

  put<T>(url: string, data: unknown, options = {}) {
    return this.httpClient.put<T>(url, data, options)
  }

  delete(url: string) {
    return this.httpClient.delete(url, {})
  }
}
