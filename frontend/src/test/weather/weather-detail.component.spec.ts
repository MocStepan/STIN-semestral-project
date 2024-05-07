import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WeatherDetailComponent} from '../../app/weather/weather-detail/weather-detail.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {WeatherService} from "../../app/weather/service/weather.service";
import {of, throwError} from "rxjs";
import {CurrentWeatherDetail} from "../../app/weather/model/CurrentWeatherDetail";
import {ForecastWeatherDetail} from "../../app/weather/model/ForecastWeatherDetal";
import {NotificationService} from "../../app/shared/notification/service/notification.service";
import {AuthService} from "../../app/auth/service/auth.service";
import {WeatherGraphComponent} from "../../app/weather/weather-graph/weather-graph.component";

describe('WeatherDetailComponent', () => {
  let component: WeatherDetailComponent;
  let fixture: ComponentFixture<WeatherDetailComponent>;
  let weatherService: WeatherService;
  let notificationService: NotificationService;
  let authService: AuthService

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        WeatherDetailComponent,
        HttpClientTestingModule,
        BrowserAnimationsModule,
        WeatherGraphComponent
      ],
      providers: [
        WeatherService
      ]

    }).compileComponents();

    fixture = TestBed.createComponent(WeatherDetailComponent);
    component = fixture.componentInstance;
    weatherService = fixture.debugElement.injector.get(WeatherService);
    notificationService = fixture.debugElement.injector.get(NotificationService);
    authService = fixture.debugElement.injector.get(AuthService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe("ngOnInit", () => {
    it("authUser is signed in", () => {
      authService.isSignedIn = jest.fn().mockReturnValue(true);

      component.ngOnInit();

      expect(authService.isSignedIn).toHaveBeenCalled();
      expect(component['isUserSignedIn']()).toEqual(true);
    })

    it('authUser is not signed in', () => {
      authService.isSignedIn = jest.fn().mockReturnValue(false);

      component.ngOnInit();

      expect(authService.isSignedIn).toHaveBeenCalled();
      expect(component['isUserSignedIn']()).toEqual(false);
    });
  });

  describe("getWeather", () => {
    it("authUser is signed in", () => {
      component['isUserSignedIn'].set(true);
      component['getCurrentWeather'] = jest.fn();
      component['getForecastWeather'] = jest.fn();

      component['getWeather']();

      expect(component['getCurrentWeather']).toHaveBeenCalled()
      expect(component['getForecastWeather']).toHaveBeenCalled()
    })

    it('authUser is not signed in', () => {
      component['isUserSignedIn'].set(true);
      component['getCurrentWeather'] = jest.fn();

      component['getWeather']();

      expect(component['getCurrentWeather']).toHaveBeenCalled()
    });
  });

  describe("getCurrentWeather", () => {
    it("should get current weather", () => {
      component['cityFormControl'].setValue("Test");
      const response = new CurrentWeatherDetail(
        new Date(),
        20,
        50,
        50,
        true
      );

      weatherService.getCurrentWeather = jest.fn().mockReturnValue(of(response));

      component['getCurrentWeather']();

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("Test");
      expect(component['currentWeather']()).toEqual(response);
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = CurrentWeatherDetail.createDefault();
      component['currentWeather'].set(defaultWeather);
      component['cityFormControl'].setValue("Test");

      weatherService.getCurrentWeather = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();

      component['getCurrentWeather']();

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("Test");
      expect(component['currentWeather']()).toEqual(defaultWeather);
      expect(notificationService.errorNotification).toHaveBeenCalledWith('City not found');
    });
  });

  describe("getForecastWeather", () => {
    it("should get forecast weather", () => {
      component['cityFormControl'].setValue("Test");
      const response = new ForecastWeatherDetail(
        [new Date()],
        [20],
        [50],
        [50]
      );

      weatherService.getForecastWeather = jest.fn().mockReturnValue(of(response));
      component['changeSaveButtonState'] = jest.fn();

      component['getForecastWeather']();

      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("Test");
      expect(component['changeSaveButtonState']).toHaveBeenCalledWith(false);
    });

    it("should show error notification when city is not found", () => {
      component['cityFormControl'].setValue("Test");

      weatherService.getForecastWeather = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();


      component['getForecastWeather']();

      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("Test");
      expect(notificationService.errorNotification).toHaveBeenCalledWith('City not found');
    });
  })

  describe("saveLocation", () => {
    it('should save location', () => {
      component['cityFormControl'].setValue("Test");

      weatherService.saveUserLocation = jest.fn().mockReturnValue(of(true));
      notificationService.successNotification = jest.fn();
      component['changeSaveButtonState'] = jest.fn();

      component['saveLocation']();

      expect(weatherService.saveUserLocation).toHaveBeenCalledWith("Test");
      expect(notificationService.successNotification).toHaveBeenCalledWith('City saved');
      expect(component['changeSaveButtonState']).toHaveBeenCalledWith(true);
    });

    it('should show error notification when city is already saved', () => {
      component['cityFormControl'].setValue("Test");

      weatherService.saveUserLocation = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();
      component['changeSaveButtonState'] = jest.fn();

      component['saveLocation']();

      expect(weatherService.saveUserLocation).toHaveBeenCalledWith("Test");
      expect(notificationService.errorNotification).toHaveBeenCalledWith('The city is already saved');
      expect(component['changeSaveButtonState']).toHaveBeenCalledWith(true);

    });
  })

  describe("changeSaveButtonState", () => {
    beforeEach(() => {
      component['saveButton'] = document.createElement('button');
      component['changeDetectorRef'].detectChanges = jest.fn();
    })

    it("should change save button state", () => {
      component['isUserSignedIn'].set(true);
      component['saveButton'].disabled = false;

      component['changeSaveButtonState'](true);

      expect(component['saveButton'].disabled).toEqual(true);
      expect(component['changeDetectorRef'].detectChanges).toHaveBeenCalled();
    });

    it("should not change save button state", () => {
      component['isUserSignedIn'].set(false);
      component['saveButton'].disabled = false;

      component['changeSaveButtonState'](true);

      expect(component['saveButton'].disabled).toEqual(false);
      expect(component['changeDetectorRef'].detectChanges).not.toHaveBeenCalled();
    });
  });
});
