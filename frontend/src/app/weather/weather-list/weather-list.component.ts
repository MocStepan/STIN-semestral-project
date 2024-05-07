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
import {Subscription} from "rxjs";
import {WeatherService} from "../service/weather.service";
import {NotificationService} from "../../shared/notification/service/notification.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {WeatherLocation} from "../model/WeatherLocation";
import {NgIf} from "@angular/common";
import {MatButton} from "@angular/material/button";
import {WeatherGraphComponent} from "../weather-graph/weather-graph.component";
import {CurrentWeatherDetail} from "../model/CurrentWeatherDetail";
import moment from "moment";
import {MatCard, MatCardContent, MatCardSubtitle, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";


@Component({
  selector: 'app-weather-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatCellDef,
    MatHeaderCellDef,
    MatHeaderRowDef,
    NgIf,
    MatButton,
    MatCard,
    MatCardContent,
    MatCardSubtitle,
    MatCardTitle,
    MatIcon,
    WeatherGraphComponent
  ],
  templateUrl: './weather-list.component.html',
  styleUrl: './weather-list.component.css'
})
export class WeatherListComponent implements OnInit, OnDestroy {
  @ViewChild(WeatherGraphComponent) weatherGraphComponent!: WeatherGraphComponent;
  protected weatherLocations: WritableSignal<WeatherLocation[]> = signal([])
  protected currentWeather: WritableSignal<CurrentWeatherDetail> = signal(CurrentWeatherDetail.createDefault())
  protected displayedColumns: string[] = ['id', 'location', 'show', 'delete'];
  protected readonly moment = moment;
  private weatherService = inject(WeatherService);
  private notificationService = inject(NotificationService)
  private subscriptions: Subscription[] = [];

  constructor() {
  }

  ngOnInit(): void {
    this.loadLocations();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  protected showCurrentWeather(location: string) {
    this.subscriptions.push(this.weatherService.getCurrentWeather(location).subscribe({
      next: (response) => {
        this.currentWeather.set(response)
      },
      error: () => {
        this.notificationService.errorNotification('City not found')
      }
    }))
    this.subscriptions.push(this.weatherService.getForecastWeather(location).subscribe({
      next: (response) => {
        this.weatherGraphComponent.createChart(response)
      },
      error: () => {
        this.notificationService.errorNotification('City not found')
      }
    }))
  }

  protected deleteUserWeatherLocation(id: number) {
    this.subscriptions.push(this.weatherService.deleteUserWeatherLocation(id).subscribe({
      next: () => {
        this.loadLocations()
      },
      error: () => {
        this.notificationService.errorNotification('City not found')
      }
    }))
  }

  private loadLocations() {
    this.subscriptions.push(this.weatherService.getUserWeatherLocations().subscribe((response) => {
      this.weatherLocations.set(response);
    }));
  }
}
