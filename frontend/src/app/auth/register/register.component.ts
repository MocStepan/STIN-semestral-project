import {ChangeDetectionStrategy, Component, inject, OnInit} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatCard} from "@angular/material/card";
import {MatFormField} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";
import {Router} from "@angular/router";
import {RegistrationForm} from "../model/RegistrationForm";

@Component({
  selector: 'app-register',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatButton,
    MatCard,
    MatFormField,
    MatIcon,
    MatInput,
    MatToolbar,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
  providers: [
    AuthService,
    FormBuilder,
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: RegisterComponent
    }
  ]
})
export class RegisterComponent implements OnInit {
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
      this.authService.register(this.formGroup.value).subscribe({
        next: () => {
          this.notificationService.successNotification("Registrace proběhla úspěšně")
          this.router.navigate(['/login'])
        },
        error: () => {
          this.notificationService.errorNotification("Registrace se nezdařila")
        }
      })
    } else {
      this.notificationService.errorNotification("Formulář obsahuje nevalidní informace")
    }
  }

  openLoginForm() {
    this.router.navigate(['/login'])
  }

  private buildFormGroup() {
    const form: RegistrationForm = {
      username: [null, [Validators.required]],
      email: [null, [Validators.email, Validators.required]],
      password: [null, [Validators.required]],
      passwordConfirmation: [null, [Validators.required]]
    }

    return this.formBuilder.group(form)
  }
}
