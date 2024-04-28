import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {LoginComponent} from "./auth/login/login.component";
import {RegisterComponent} from "./auth/register/register.component";
import {WeatherDetailComponent} from "./weather/weather-detail/weather-detail.component";
import {WeatherListComponent} from "./weather/weather-list/weather-list.component";

const routes: Routes = [
  {path: '', component: WeatherDetailComponent},
  {path: 'login', component: LoginComponent},
  {path: 'registration', component: RegisterComponent},
  {path: 'weather', component: WeatherListComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
