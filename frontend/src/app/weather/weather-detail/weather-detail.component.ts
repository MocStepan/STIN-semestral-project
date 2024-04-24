import {
  ChangeDetectionStrategy,
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
  WritableSignal
} from '@angular/core';
import {MatFormField, MatPrefix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {FormControl, NG_VALUE_ACCESSOR, ReactiveFormsModule} from "@angular/forms";
import {WeatherService} from "../service/weather.service";
import {CurrentWeatherDetail} from "../model/CurrentWeatherDetail";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";
import {WeatherGraphComponent} from "../weather-graph/weather-graph.component";
import {Subscription} from "rxjs";
import {AuthService} from "../../auth/service/auth.service";
import {RegisterComponent} from "../../auth/register/register.component";
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
    MatCardSubtitle
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
  protected currentSignal: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)
  private subscriptions: Subscription[] = [];

  constructor(private weatherService: WeatherService,
              private notificationService: FrontendNotificationService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    this.isUserSignedIn.set(this.authService.isUserSignedIn())
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

  private getCurrentWeather() {
    this.subscriptions.push(this.weatherService.getCurrentWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.currentSignal.set(response)
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

  protected readonly moment = moment;
}
