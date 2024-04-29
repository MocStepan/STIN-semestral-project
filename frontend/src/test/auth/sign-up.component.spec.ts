import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SignUpComponent} from '../../app/auth/sign-up/sign-up.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "../../app/auth/service/auth.service";
import {FormBuilder} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {NotificationService} from "../../app/shared/notification/service/notification.service";
import {Router} from "@angular/router";
import {of, throwError} from "rxjs";
import {SignUpForm} from "../../app/auth/model/SignUpForm";

describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;
  let notificationService: NotificationService;
  let authService: AuthService
  let router: Router;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignUpComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SignUpComponent);
    notificationService = fixture.debugElement.injector.get(NotificationService)
    authService = fixture.debugElement.injector.get(AuthService);
    router = fixture.debugElement.injector.get(Router);
    formBuilder = fixture.debugElement.injector.get(FormBuilder)
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('onSubmit', () => {
    it('should sign up', () => {
      const signUpForm = {
        username: "test",
        email: "test@test.cz",
        password: "password",
        passwordConfirmation: "password"
      } as SignUpForm
      component['formGroup'] = formBuilder.group(signUpForm)

      authService.signUp = jest.fn().mockReturnValue(of({}));
      notificationService.successNotification = jest.fn();
      router.navigate = jest.fn()

      component.onSubmit();

      expect(authService.signUp).toHaveBeenCalled()
      expect(notificationService.successNotification).toHaveBeenCalledWith('Registration was successful');
      expect(router.navigate).toHaveBeenCalledWith(['/signIn']);
    });

    it('should throw error', () => {
      const signUpForm = {
        username: "test",
        email: "test@test.cz",
        password: "password",
        passwordConfirmation: "password"
      } as SignUpForm
      component['formGroup'] = formBuilder.group(signUpForm)

      notificationService.errorNotification = jest.fn();
      authService.signUp = jest.fn().mockReturnValue(throwError(() => ({status: 500})));

      component.onSubmit();

      expect(authService.signUp).toHaveBeenCalled();
      expect(notificationService.errorNotification).toHaveBeenCalledWith('Registration failed');
    })

    it('invalid formGroup', () => {
      notificationService.errorNotification = jest.fn();

      component.onSubmit();

      expect(notificationService.errorNotification).toHaveBeenCalledWith('The form contains invalid information');
    })
  })

  describe('openSignInForm', () => {
    it('should open signInForm', () => {
      router.navigate = jest.fn();

      component.openSignInForm();

      expect(router.navigate).toHaveBeenCalledWith(['/signIn']);
    })
  })

});
