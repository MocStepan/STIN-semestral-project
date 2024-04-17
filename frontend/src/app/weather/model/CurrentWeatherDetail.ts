export class CurrentWeatherDetail {
  time: Date
  temperature: number
  cloudCover: number
  windSpeed: number
  isDay: boolean

  constructor(time: Date, temperature: number, cloudCover: number, windSpeed: number, isDay: boolean) {
    this.time = time
    this.temperature = temperature
    this.cloudCover = cloudCover
    this.windSpeed = windSpeed
    this.isDay = isDay
  }

  static createDefault(): CurrentWeatherDetail {
    return new CurrentWeatherDetail(new Date(), 0, 0, 0, false)
  }
}
