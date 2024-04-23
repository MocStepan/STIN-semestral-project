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
import {MatCard, MatCardContent, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {WeatherService} from "../service/weather.service";
import {CurrentWeatherDetail} from "../model/CurrentWeatherDetail";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";
import {WeatherGraphComponent} from "../weather-graph/weather-graph.component";
import {Subscription} from "rxjs";

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
    WeatherGraphComponent
  ],
  providers: [WeatherService],
  templateUrl: './weather-detail.component.html',
  styleUrl: './weather-detail.component.css'
})
export class WeatherDetailComponent implements OnInit, OnDestroy {
  @ViewChild(WeatherGraphComponent) weatherGraphComponent!: WeatherGraphComponent;

  protected cityFormControl = new FormControl();
  protected currentSignal: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  private weatherService = inject(WeatherService);
  private notificationService = inject(FrontendNotificationService);
  private subscriptions: Subscription[] = [];

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  getCurrentWeather() {
    this.subscriptions.push(this.weatherService.getCurrentWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.currentSignal.set(response)
      },
      error: () => {
        this.notificationService.errorNotification('Město nenalezeno')
      }
    }))
  }

  getForecastWeather() {
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
