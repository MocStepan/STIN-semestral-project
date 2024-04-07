import {inject, Injectable} from "@angular/core";
import {HttpService} from "../../http/service/http.service";
import {API_URL} from "../../../config";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private httpService: HttpService) {
  }
  
  getAuth(): Observable<string> {
    return this.httpService.get(API_URL + '/actual-weather')
  }
}
