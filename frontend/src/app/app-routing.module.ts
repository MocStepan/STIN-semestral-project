import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {LoginComponent} from "./auth/login/login.component";
import {RegisterComponent} from "./auth/register/register.component";
import {WeatherListComponent} from "./weather/weather-list/weather-list.component";

const routes: Routes = [
  {path: '', component: WeatherListComponent},
  {path: 'login', component: LoginComponent},
  {path: 'registration', component: RegisterComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
