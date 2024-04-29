import {RouterModule, Routes} from "@angular/router";
import {NgModule} from "@angular/core";
import {WeatherDetailComponent} from "./weather/weather-detail/weather-detail.component";
import {WeatherListComponent} from "./weather/weather-list/weather-list.component";
import {SignInComponent} from "./auth/sign-in/sign-in.component";
import {SignUpComponent} from "./auth/sign-up/sign-up.component";

const routes: Routes = [
  {path: '', component: WeatherDetailComponent},
  {path: 'signIn', component: SignInComponent},
  {path: 'signUp', component: SignUpComponent},
  {path: 'weather', component: WeatherListComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
