import {ChangeDetectionStrategy, Component, OnInit} from "@angular/core";
import {AuthService} from "../auth/service/auth.service";

@Component({
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  providers: [AuthService]
})
export class UserListComponent {

  constructor(private authService: AuthService) {
  }

  getWeather() {
    this.authService.getAuth().subscribe((response) => {
      console.log(response)
    })
  }
}
