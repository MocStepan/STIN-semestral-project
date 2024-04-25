import {Component, OnInit} from '@angular/core';
import {ForecastWeatherDetail} from "../model/ForecastWeatherDetal";
import {Chart, registerables} from 'chart.js';
import moment from "moment";

@Component({
  selector: 'app-weather-graph',
  standalone: true,
  imports: [],
  templateUrl: './weather-graph.component.html',
  styleUrl: './weather-graph.component.css'
})
export class WeatherGraphComponent implements OnInit {
  protected chart: any;

  ngOnInit() {
    Chart.register(...registerables);
    this.createChart(ForecastWeatherDetail.createDefault());
  }

  createChart(data: ForecastWeatherDetail) {
    if (this.chart != undefined) {
      this.chart.destroy();
    }
    this.chart = new Chart("MyChart", {
      type: "line", //this denotes tha type of chart

      data: {// values on X-Axis
        labels: data.time.map(time => moment(time).format("DD/MM")),
        datasets: [
          {
            label: "Min teplota (°C)",
            data: data.minTemperature,
            backgroundColor: 'blue',
            borderColor: 'grey'
          },
          {
            label: "Max teplota (°C)",
            data: data.maxTemperature,
            backgroundColor: 'red',
            borderColor: 'grey'
          },
          {
            label: "Max rychlsot větru (m/s)",
            data: data.maxWindSpeed,
            backgroundColor: 'black',
            borderColor: 'grey'
          }
        ]
      },
      options: {
        scales: {
          y: {
            beginAtZero: true
          }
        },
        responsive: true,
        aspectRatio: 1
      }
    });
  }
}
