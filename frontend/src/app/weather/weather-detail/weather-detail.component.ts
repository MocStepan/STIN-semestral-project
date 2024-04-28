import {ChangeDetectionStrategy, Component, OnDestroy, OnInit, signal, ViewChild, WritableSignal} from '@angular/core';
import {MatFormField, MatPrefix, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";
import {WeatherService} from "../service/weather.service";
import {CurrentWeatherDetail} from "../model/CurrentWeatherDetail";
import {NotificationService} from "../../shared/notification/service/notification.service";
import {WeatherGraphComponent} from "../weather-graph/weather-graph.component";
import {Subscription} from "rxjs";
import {AuthService} from "../../auth/service/auth.service";
import moment from "moment";

@Component({
  selector: 'app-weather-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatFormField,
    MatInput,
    MatButton,
    MatCard,
    MatIcon,
    MatToolbar,
    NgIf,
    ReactiveFormsModule,
    MatPrefix,
    MatCardContent,
    MatCardTitle,
    WeatherGraphComponent,
    MatCardSubtitle,
    MatSuffix
  ],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: WeatherDetailComponent
    }
  ],
  templateUrl: './weather-detail.component.html',
  styleUrl: './weather-detail.component.css'
})
export class WeatherDetailComponent implements OnInit, OnDestroy {
  @ViewChild(WeatherGraphComponent) weatherGraphComponent!: WeatherGraphComponent;
  protected cityFormControl = new FormControl();
  protected currentWeather: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)
  protected readonly moment = moment;
  private subscriptions: Subscription[] = [];

  constructor(private weatherService: WeatherService,
              private notificationService: NotificationService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.isUserSignedIn.set(this.authService.isSignedIn())
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getWeather() {
    this.getCurrentWeather()
    if (this.isUserSignedIn()) {
      this.getForecastWeather()
    }
  }

  protected saveLocation() {
    this.subscriptions.push(this.weatherService.saveUserLocation(this.cityFormControl.value).subscribe({
      next: () => {
        this.notificationService.successNotification('Město uloženo')
      },
      error: () => {
        this.notificationService.errorNotification('Město nelze uložit')
      }
    }))
  }

  private getCurrentWeather() {
    this.subscriptions.push(this.weatherService.getCurrentWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.currentWeather.set(response)
      },
      error: () => {
        this.notificationService.errorNotification('Město nenalezeno')
      }
    }))
  }

  private getForecastWeather() {
    this.subscriptions.push(this.weatherService.getForecastWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.weatherGraphComponent.createChart(response)
      },
      error: () => {
        this.notificationService.errorNotification('Město nenalezeno')
      }
    }))
  }
}
