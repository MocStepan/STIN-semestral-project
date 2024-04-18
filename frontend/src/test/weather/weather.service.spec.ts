import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {HttpService} from "../../app/shared/http/service/http.service";
import {WeatherService} from "../../app/weather/service/weather.service";
import {CurrentWeatherDetail} from "../../app/weather/model/CurrentWeatherDetail";
import {ForecastWeatherDetail} from "../../app/weather/model/ForecastWeatherDetal";

describe('WeatherService', () => {
  let weatherService: WeatherService;
  let httpService: HttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [WeatherService, HttpService]
    });

    weatherService = TestBed.inject(WeatherService);
    httpService = TestBed.inject(HttpService)
  });

  it('get current weather', () => {
    const currentWeather = CurrentWeatherDetail.createDefault()
    httpService.get = jest.fn().mockReturnValue(currentWeather);

    weatherService.getCurrentWeather("Test");

    expect(httpService.get).toHaveBeenCalled()
  });

  it('get forecast weather', () => {
    const forecastWeather = ForecastWeatherDetail.createDefault()
    httpService.get = jest.fn().mockReturnValue(forecastWeather);

    weatherService.getForecastWeather("Test");

    expect(httpService.get).toHaveBeenCalled()
  });
});
