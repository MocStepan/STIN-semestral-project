import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatCard} from "@angular/material/card";
import {MatFormField, MatPrefix} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatToolbar} from "@angular/material/toolbar";
import {NgIf} from "@angular/common";
import {NotificationService} from "../../shared/notification/service/notification.service";
import {Router} from "@angular/router";
import {SignUpForm} from "../model/SignUpForm";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-sign.up',
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
  templateUrl: './sign-up.component.html',
  styleUrl: '../style/auth.component.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SignUpComponent
    }
  ]
})
export class SignUpComponent implements OnInit, OnDestroy {
  protected formGroup!: FormGroup
  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder,
              private authService: AuthService,
              private notificationService: NotificationService,
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
      this.subscriptions.push(this.authService.signUp(this.formGroup.value).subscribe({
        next: () => {
          this.notificationService.successNotification("Registration was successful")
          this.router.navigate(['/signIn'])
        },
        error: () => {
          this.notificationService.errorNotification("Registration failed")
        }
      }))
    } else {
      this.notificationService.errorNotification("The form contains invalid information")
    }
  }

  openSignInForm() {
    this.router.navigate(['/signIn'])
  }

  private buildFormGroup() {
    const form: SignUpForm = {
      username: [null, [Validators.required]],
      email: [null, [Validators.email, Validators.required]],
      password: [null, [Validators.required]],
      passwordConfirmation: [null, [Validators.required]]
    }

    return this.formBuilder.group(form)
  }
}
