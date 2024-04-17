import {ChangeDetectionStrategy, Component, inject, OnInit, signal, WritableSignal} from '@angular/core';
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
import {ForecastWeatherDetail} from "../model/ForecastWeatherDetal";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";

@Component({
  selector: 'app-weather-list',
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
    MatCardTitle
  ],
  providers: [WeatherService],
  templateUrl: './weather-list.component.html',
  styleUrl: './weather-list.component.css'
})
export class WeatherListComponent implements OnInit {
  private weatherService = inject(WeatherService);
  private notificationService = inject(FrontendNotificationService);
  protected cityForm = new FormControl();
  protected currentSignal: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  protected forecastSignal: WritableSignal<ForecastWeatherDetail> = signal(ForecastWeatherDetail.createDefault())

  ngOnInit(): void {
  }

  getCurrentWeather() {
    this.weatherService.getCurrentWeather(this.cityForm.value).subscribe({
      next: (response) => {
        this.currentSignal.set(response)
      },
      error: (error) => {
        this.notificationService.errorNotification('Město nenalezeno')
      }
    })
    this.weatherService.getForecastWeather(this.cityForm.value).subscribe({
      next: (response) => {
        this.forecastSignal.set(response)
      },
      error: (error) => {
        this.notificationService.errorNotification('Město nenalezeno')
      }
    })
  }
}
