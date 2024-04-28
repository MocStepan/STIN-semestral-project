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
      const signedSpy = jest.spyOn(authService, 'isSignedIn').mockReturnValue(true);

      fixture.ngZone?.run(() => {
        component.ngOnInit();
      });

      expect(signedSpy).toHaveBeenCalled();
      expect(component['isUserSignedIn']()).toEqual(true);
    })

    it('authUser is not signed in', () => {
      const signedSpy = jest.spyOn(authService, 'isSignedIn').mockReturnValue(false);

      fixture.ngZone?.run(() => {
        component.ngOnInit();
      });

      expect(signedSpy).toHaveBeenCalled();
      expect(component['isUserSignedIn']()).toEqual(false);
    });
  });

  describe("getWeather", () => {
    it("authUser is signed in", () => {
      component['isUserSignedIn'].set(true);
      component['getCurrentWeather'] = jest.fn();
      component['getForecastWeather'] = jest.fn();

      fixture.ngZone?.run(() => {
        component['getWeather']();
      });

      expect(component['getCurrentWeather']).toHaveBeenCalled()
      expect(component['getForecastWeather']).toHaveBeenCalled()
    })

    it('authUser is not signed in', () => {
      component['isUserSignedIn'].set(true);
      component['getCurrentWeather'] = jest.fn();

      fixture.ngZone?.run(() => {
        component['getWeather']();
      });

      expect(component['getCurrentWeather']).toHaveBeenCalled()
    });
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
        component['getCurrentWeather']();
      })

      expect(weatherService.getCurrentWeather).toHaveBeenCalledWith("Prague");
      expect(component['currentWeather']()).toEqual(response);
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = CurrentWeatherDetail.createDefault();
      component['currentWeather'].set(defaultWeather);
      component['cityFormControl'].setValue("Prague");
      const error = {status: 500}

      const weatherSpy = weatherService.getCurrentWeather = jest.fn().mockReturnValue(throwError(() => error));
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      fixture.ngZone?.run(() => {
        component['getCurrentWeather']();
      })

      expect(weatherSpy).toHaveBeenCalledWith("Prague");
      expect(component['currentWeather']()).toEqual(defaultWeather);
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

      weatherService.getForecastWeather = jest.fn().mockReturnValue(of(response));

      fixture.ngZone?.run(() => {
        component['getForecastWeather']();
      })

      expect(weatherService.getForecastWeather).toHaveBeenCalledWith("Prague");
    });

    it("should show error notification when city is not found", () => {
      const defaultWeather = ForecastWeatherDetail.createDefault();
      component['cityFormControl'].setValue("Prague");
      const error = {status: 500}

      const weatherSpy = weatherService.getForecastWeather = jest.fn().mockReturnValue(throwError(() => error));
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      fixture.ngZone?.run(() => {
        component['getForecastWeather']();
      })

      expect(weatherSpy).toHaveBeenCalledWith("Prague");
      expect(notificationSpy).toHaveBeenCalledWith('Město nenalezeno');
    });
  })
});
