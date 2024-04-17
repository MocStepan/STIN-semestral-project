import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {FormBuilder, FormGroup, FormsModule, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from "@angular/forms";
import {LoginForm} from "../model/LoginForm";
import {MatFormField, MatFormFieldModule, MatPrefix} from "@angular/material/form-field";
import {MatCard} from "@angular/material/card";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon, MatIconModule} from "@angular/material/icon";
import {MatInput, MatInputModule} from "@angular/material/input";
import {MatButton, MatButtonModule} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";
import {Router} from "@angular/router";
import {AppModule} from "../../app.module";

@Component({
  selector: 'app-login',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatFormField,
    MatCard,
    MatToolbar,
    MatIcon,
    MatInput,
    MatButton,
    NgIf,
    ReactiveFormsModule,
    MatPrefix
  ],
  templateUrl: './login.component.html',
  styleUrl: '../style/auth.component.css',
  providers: [
    AuthService,
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: LoginComponent
    }
  ]
})
export class LoginComponent implements OnInit {
  formGroup!: FormGroup
  private formBuilder = inject(FormBuilder)
  private authService = inject(AuthService)
  private notificationService = inject(FrontendNotificationService)
  private router = inject(Router)

  ngOnInit(): void {
    this.formGroup = this.buildFormGroup()
  }

  onSubmit() {
    if (this.formGroup.valid) {
      this.authService.login(this.formGroup.value).subscribe({
        next: () => {
          this.notificationService.successNotification("Ůspěšně jste se přihlásili")
          this.router.navigate(['/'])
        },
        error: () => {
          this.notificationService.errorNotification("Špatné heslo nebo email")
        }
      })
    } else {
      this.notificationService.errorNotification("Formulář obsahuje nevalidní informace")
    }
  }

  openRegistrationForm() {
    this.router.navigate(['/registration'])
  }

  private buildFormGroup() {
    const form: LoginForm = {
      email: [null, [Validators.email, Validators.required]],
      password: [null, [Validators.required]]
    }

    return this.formBuilder.group(form)
  }
}
