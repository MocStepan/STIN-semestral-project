import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SignInComponent} from '../../app/auth/sign-in/sign-in.component';
import {AuthService} from "../../app/auth/service/auth.service";
import {FormBuilder} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {of, throwError} from "rxjs";
import {NotificationService} from "../../app/shared/notification/service/notification.service";
import {SignInForm} from "../../app/auth/model/SignInForm";

describe('SignInComponent', () => {
  let component: SignInComponent;
  let fixture: ComponentFixture<SignInComponent>;
  let notificationService: NotificationService;
  let authService: AuthService
  let router: Router;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SignInComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SignInComponent);
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

  describe('signIn', () => {
    it('should sign in', () => {
      const signInForm = {email: "test@test.cz", password: "password"} as SignInForm
      component['formGroup'] = formBuilder.group(signInForm)

      authService.signIn = jest.fn().mockReturnValue(of({}));
      notificationService.successNotification = jest.fn();
      router.navigate = jest.fn()

      component.onSubmit();

      expect(authService.signIn).toHaveBeenCalled()
      expect(notificationService.successNotification).toHaveBeenCalledWith('You have successfully logged in');
      expect(router.navigate).toHaveBeenCalledWith(['/']);
    });

    it('invalid password', () => {
      const signInForm = {email: "test@test.cz", password: "password"} as SignInForm
      component['formGroup'] = formBuilder.group(signInForm)

      authService.signIn = jest.fn().mockReturnValue(throwError(() => ({status: 500})));
      notificationService.errorNotification = jest.fn();

      component.onSubmit();

      expect(authService.signIn).toHaveBeenCalled();
      expect(notificationService.errorNotification).toHaveBeenCalledWith('Wrong password or email');
    })

    it('invalid formGroup', () => {
      notificationService.errorNotification = jest.fn();

      component.onSubmit();

      expect(notificationService.errorNotification).toHaveBeenCalledWith('The form contains invalid information');
    })
  })

  describe('openSignUpForm', () => {
    it('should open signUpForm', () => {
      router.navigate = jest.fn()

      component.openSignUpForm();

      expect(router.navigate).toHaveBeenCalledWith(['/signUp']);
    })
  })

});
