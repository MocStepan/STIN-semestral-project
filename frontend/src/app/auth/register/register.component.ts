import {ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatCard} from "@angular/material/card";
import {MatFormField, MatPrefix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {FrontendNotificationService} from "../../shared/frontend-notification/service/frontend-notification.service";
import {Router} from "@angular/router";
import {RegistrationForm} from "../model/RegistrationForm";
import {Subscription} from "rxjs";

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
    ReactiveFormsModule,
    MatPrefix
  ],
  templateUrl: './register.component.html',
  styleUrl: '../style/auth.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: RegisterComponent
    }
  ]
})
export class RegisterComponent implements OnInit, OnDestroy {
  protected formGroup!: FormGroup
  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: FrontendNotificationService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.formGroup = this.buildFormGroup()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  onSubmit() {
    if (this.formGroup.valid) {
      this.subscriptions.push(this.authService.register(this.formGroup.value).subscribe({
        next: () => {
          this.notificationService.successNotification("Registrace proběhla úspěšně")
          this.router.navigate(['/login'])
        },
        error: () => {
          this.notificationService.errorNotification("Registrace se nezdařila")
        }
      }))
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
