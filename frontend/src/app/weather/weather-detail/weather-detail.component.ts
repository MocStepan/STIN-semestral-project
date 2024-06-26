import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  signal,
  ViewChild,
  WritableSignal
} from '@angular/core';
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
export class WeatherDetailComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild(WeatherGraphComponent) weatherGraphComponent!: WeatherGraphComponent;
  @ViewChild("saveButton") saveButton!: HTMLButtonElement;
  protected cityFormControl = new FormControl();
  protected currentWeather: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)
  protected readonly moment = moment;

  private subscriptions: Subscription[] = [];
  private weatherService = inject(WeatherService)
  private notificationService = inject(NotificationService)
  private authService = inject(AuthService)
  private changeDetectorRef = inject(ChangeDetectorRef)

  ngOnInit(): void {
    this.isUserSignedIn.set(this.authService.isSignedIn())
  }

  ngAfterViewInit(): void {
    this.changeSaveButtonState(true)
    this.changeDetectorRef.detectChanges()
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
        this.notificationService.successNotification('City saved')
        this.changeSaveButtonState(true)
      },
      error: () => {
        this.changeSaveButtonState(true)
        this.notificationService.errorNotification('The city is already saved')
      }
    }))
  }

  private changeSaveButtonState(state: boolean) {
    if (this.isUserSignedIn()) {
      this.saveButton.disabled = state
      this.changeDetectorRef.detectChanges()
    }
  }

  private getCurrentWeather() {
    this.subscriptions.push(this.weatherService.getCurrentWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.currentWeather.set(response)
      },
      error: () => {
        this.notificationService.errorNotification('City not found')
      }
    }))
  }

  private getForecastWeather() {
    this.subscriptions.push(this.weatherService.getForecastWeather(this.cityFormControl.value).subscribe({
      next: (response) => {
        this.changeSaveButtonState(false)
        this.weatherGraphComponent.createChart(response)
      },
      error: () => {
        this.changeSaveButtonState(true)
        this.notificationService.errorNotification('City not found')
      }
    }))
  }
}
