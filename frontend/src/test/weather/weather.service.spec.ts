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

  it('getCurrentWeather', () => {
    const currentWeather = CurrentWeatherDetail.createDefault()
    httpService.get = jest.fn().mockReturnValue(currentWeather);

    weatherService.getCurrentWeather("Test");

    expect(httpService.get).toHaveBeenCalled()
  });

  it('getForecastWeather', () => {
    const forecastWeather = ForecastWeatherDetail.createDefault()
    httpService.get = jest.fn().mockReturnValue(forecastWeather);

    weatherService.getForecastWeather("Test");

    expect(httpService.get).toHaveBeenCalled()
  });

  it('saveUserLocation', () => {
    httpService.post = jest.fn().mockReturnValue(true);

    weatherService.saveUserLocation("Test");

    expect(httpService.post).toHaveBeenCalled()
  });

  it('getUserWeatherLocations', () => {
    httpService.get = jest.fn().mockReturnValue([]);

    weatherService.getUserWeatherLocations();

    expect(httpService.get).toHaveBeenCalled()
  });

  it('deleteUserWeatherLocation', () => {
    httpService.delete = jest.fn().mockReturnValue(true);

    weatherService.deleteUserWeatherLocation(1);

    expect(httpService.delete).toHaveBeenCalled()
  });
});
