export class ForecastWeatherDetail {
  time: Array<Date>
  maxTemperature: Array<number>
  minTemperature: Array<number>
  maxWindSpeed: Array<number>


  constructor(
    time: Array<Date>,
    maxTemperature: Array<number>,
    minTemperature: Array<number>,
    maxWindSpeed: Array<number>
  ) {
    this.time = time;
    this.maxTemperature = maxTemperature;
    this.minTemperature = minTemperature;
    this.maxWindSpeed = maxWindSpeed;
  }

  static createDefault(): ForecastWeatherDetail {
    return new ForecastWeatherDetail(
      new Array<Date>(),
      new Array<number>(),
      new Array<number>(),
      new Array<number>()
    )
  }
}
