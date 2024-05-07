import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WeatherListComponent} from '../../app/weather/weather-list/weather-list.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {WeatherGraphComponent} from "../../app/weather/weather-graph/weather-graph.component";
import {WeatherService} from "../../app/weather/service/weather.service";
import {NotificationService} from "../../app/shared/notification/service/notification.service";
import {WeatherLocation} from "../../app/weather/model/WeatherLocation";
import {of, throwError} from "rxjs";
import {CurrentWeatherDetail} from "../../app/weather/model/CurrentWeatherDetail";
import {ForecastWeatherDetail} from "../../app/weather/model/ForecastWeatherDetal";

describe('WeatherListComponent', () => {
  let component: WeatherListComponent;
  let fixture: ComponentFixture<WeatherListComponent>;
  let weatherService: WeatherService;
  let notificationService: NotificationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        WeatherListComponent,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        WeatherGraphComponent
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(WeatherListComponent);
    weatherService = fixture.debugElement.injector.get(WeatherService);
    notificationService = fixture.debugElement.injector.get(NotificationService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe("ngOnInit", () => {
    it('should load locations', () => {
      const locations = [new WeatherLocation(1, 'Test')]
      weatherService.getUserWeatherLocations = jest.fn().mockReturnValue(of(locations));

      component.ngOnInit();

      expect(weatherService.getUserWeatherLocations).toHaveBeenCalled();
      expect(component['weatherLocations']()).toEqual(locations);
    });
  })

  describe("loadLocations", () => {
    it('should load locations', () => {
      const locations = [new WeatherLocation(1, 'Test')]
      weatherService.getUserWeatherLocations = jest.fn().mockReturnValue(of(locations));

      component['loadLocations']();

      expect(weatherService.getUserWeatherLocations).toHaveBeenCalled();
      expect(component['weatherLocations']()).toEqual(locations);
    });
  })

  describe("showWeather", () => {
    it("should show current weather", () => {
      const currentWeatherDetail = new CurrentWeatherDetail(
        new Date(),
        20,
        50,
        50,
        true
      );

      const forecastWeatherDetail = new ForecastWeatherDetail(
        [new Date()],
        [20],
        [50],
        [50]
      );

      weatherService.getCurrentWeather = jest.fn().mockReturnValue(of(currentWeatherDetail));
      weatherService.getForecastWeather = jest.fn().mockReturnValue(of(forecastWeatherDetail));

      component['showWeather']('test')

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("test");
      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("test");
      expect(component['currentWeather']()).toEqual(currentWeatherDetail);
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = CurrentWeatherDetail.createDefault();
      component['currentWeather'].set(defaultWeather);

      weatherService.getCurrentWeather = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      weatherService.getForecastWeather = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();

      component['showWeather']('test')

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("test");
      expect(component['currentWeather']()).toEqual(defaultWeather);
      expect(notificationService.errorNotification).toHaveBeenCalledWith('City not found');
      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("test");
      expect(notificationService.errorNotification).toHaveBeenCalledWith('City not found');
    });
  });

  describe("deleteUserWeatherLocation", () => {
    it('should delete location and loadLocations', () => {
      const locations = [new WeatherLocation(1, 'Test')]
      weatherService.getUserWeatherLocations = jest.fn().mockReturnValue(of(locations));
      weatherService.deleteUserWeatherLocation = jest.fn().mockReturnValue(of(true))

      component['deleteUserWeatherLocation'](1);

      expect(weatherService.deleteUserWeatherLocation).toHaveBeenCalledWith(1);
      expect(weatherService.getUserWeatherLocations).toHaveBeenCalled();
      expect(component['weatherLocations']()).toEqual(locations);
    });

    it('should show error notification when city is not found', () => {
      weatherService.deleteUserWeatherLocation = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();

      component['deleteUserWeatherLocation'](1);

      expect(weatherService.deleteUserWeatherLocation).toHaveBeenCalledWith(1);
      expect(notificationService.errorNotification).toHaveBeenCalledWith('City not found');
    });
  })
});
