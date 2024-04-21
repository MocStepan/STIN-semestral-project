import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WeatherDetailComponent} from '../../app/weather/weather-detail/weather-detail.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {WeatherService} from "../../app/weather/service/weather.service";
import {of, throwError} from "rxjs";
import {CurrentWeatherDetail} from "../../app/weather/model/CurrentWeatherDetail";
import {ForecastWeatherDetail} from "../../app/weather/model/ForecastWeatherDetal";
import {
  FrontendNotificationService
} from "../../app/shared/frontend-notification/service/frontend-notification.service";

describe('WeatherDetailComponent', () => {
  let component: WeatherDetailComponent;
  let fixture: ComponentFixture<WeatherDetailComponent>;
  let weatherService: WeatherService;
  let notificationService: FrontendNotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WeatherDetailComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        WeatherService
      ]

    }).compileComponents();

    fixture = TestBed.createComponent(WeatherDetailComponent);
    component = fixture.componentInstance;
    weatherService = fixture.debugElement.injector.get(WeatherService);
    notificationService = fixture.debugElement.injector.get(FrontendNotificationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe("getCurrentWeather", () => {
    it("should get current weather", () => {
      component['cityFormControl'].setValue("Prague");
      const response = new CurrentWeatherDetail(
        new Date(),
        20,
        50,
        50,
        true
      );

      weatherService.getCurrentWeather = jest.fn().mockReturnValue(of(response));

      fixture.ngZone?.run(() => {
        component.getWeather();
      })

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("Prague");
      expect(component['currentSignal']()).toEqual(response);
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = CurrentWeatherDetail.createDefault();
      component['currentSignal'].set(defaultWeather);
      component['cityFormControl'].setValue("Prague");
      const error = {status: 500}

      const weatherSpy = weatherService.getCurrentWeather = jest.fn().mockReturnValue(throwError(() => error));
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      fixture.ngZone?.run(() => {
        component.getWeather();
      })

      expect(weatherSpy).toHaveBeenCalledWith("Prague");
      expect(component['currentSignal']()).toEqual(defaultWeather);
      expect(notificationSpy).toHaveBeenCalledWith('Město nenalezeno');
    });
  });

  describe("getForecastWeather", () => {
    it("should get forecast weather", () => {
      component['cityFormControl'].setValue("Prague");
      const response = new ForecastWeatherDetail(
        [new Date()],
        [20],
        [50],
        [50]
      );
      const createChartSpy = jest.spyOn(component.weatherGraphComponent, 'createChart');

      weatherService.getForecastWeather = jest.fn().mockReturnValue(of(response));

      fixture.ngZone?.run(() => {
        component.getWeather();
      })

      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("Prague");
      expect(createChartSpy).toHaveBeenCalledWith(response);
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = ForecastWeatherDetail.createDefault();
      component['cityFormControl'].setValue("Prague");
      const error = {status: 500}

      const weatherSpy = weatherService.getForecastWeather = jest.fn().mockReturnValue(throwError(() => error));
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      fixture.ngZone?.run(() => {
        component.getWeather();
      })

      expect(weatherSpy).toHaveBeenCalledWith("Prague");
      expect(notificationSpy).toHaveBeenCalledWith('Město nenalezeno');
    });
  })
});
