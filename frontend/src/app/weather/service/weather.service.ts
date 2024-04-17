import {inject, Injectable} from "@angular/core";
import {HttpService} from "../../shared/http/service/http.service";
import {BASE_API_URL} from "../../../config";
import {Observable} from "rxjs";
import {CurrentWeatherDetail} from "../model/CurrentWeatherDetail";
import {ForecastWeatherDetail} from "../model/ForecastWeatherDetal";

@Injectable({
  providedIn: 'root'
})
export class WeatherService {

  private httpService = inject(HttpService);
  private rootUrl = BASE_API_URL + 'weather/'


  getCurrentWeather(city: string): Observable<CurrentWeatherDetail> {
    return this.httpService.get(this.rootUrl + 'current/' + city)
  }

  getForecastWeather(city: string): Observable<ForecastWeatherDetail> {
    return this.httpService.get(this.rootUrl + 'forecast/' + city)
  }
}
