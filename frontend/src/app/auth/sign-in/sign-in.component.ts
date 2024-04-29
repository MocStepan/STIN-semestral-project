import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {FormBuilder, FormGroup, NG_VALUE_ACCESSOR, ReactiveFormsModule, Validators} from "@angular/forms";
import {SignInForm} from "../model/SignInForm";
import {MatFormField, MatPrefix} from "@angular/material/form-field";
import {MatCard} from "@angular/material/card";
import {MatToolbar} from "@angular/material/toolbar";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {NgIf} from "@angular/common";
import {NotificationService} from "../../shared/notification/service/notification.service";
import {Router} from "@angular/router";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-sign-in',
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
  templateUrl: './sign-in.component.html',
  styleUrl: '../style/auth.component.css',
  providers: [
    AuthService,
    {
      provide: NG_VALUE_ACCESSOR,
      multi: true,
      useExisting: SignInComponent
    }
  ]
})
export class SignInComponent implements OnInit, OnDestroy {
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
      this.subscriptions.push(this.authService.signIn(this.formGroup.value).subscribe({
        next: () => {
          this.notificationService.successNotification("You have successfully logged in")
          this.router.navigate(['/'])
        },
        error: () => {
          this.notificationService.errorNotification("Wrong password or email")
        }
      }))
    } else {
      this.notificationService.errorNotification("The form contains invalid information")
    }
  }

  openSignUpForm() {
    this.router.navigate(['/signUp'])
  }

  private buildFormGroup() {
    const form: SignInForm = {
      email: [null, [Validators.email, Validators.required]],
      password: [null, [Validators.required]]
    }

    return this.formBuilder.group(form)
  }
}
