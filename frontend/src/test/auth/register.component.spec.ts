import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RegisterComponent} from '../../app/auth/register/register.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "../../app/auth/service/auth.service";
import {FormBuilder} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {
  FrontendNotificationService
} from "../../app/shared/frontend-notification/service/frontend-notification.service";
import {Router} from "@angular/router";
import {LoginForm} from "../../app/auth/model/LoginForm";
import {of, throwError} from "rxjs";
import {RegistrationForm} from "../../app/auth/model/RegistrationForm";

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let notificationService: FrontendNotificationService;
  let authService: AuthService
  let router: Router;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    notificationService = fixture.debugElement.injector.get(FrontendNotificationService)
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
    it('should register', () => {
      const validFormValue = {
        username: "test",
        email: "test@test.cz",
        password: "password",
        passwordConfirmation: "password"
      } as RegistrationForm
      component.formGroup = formBuilder.group(validFormValue)
      fixture.detectChanges()

      authService.register = jest.fn().mockReturnValue(of({}));
      const notificationSpy = jest.spyOn(notificationService, 'successNotification');
      const routerSpy = jest.spyOn(router, 'navigate')

      fixture.ngZone?.run(() => {
        component.onSubmit();
      })

      expect(authService.register).toHaveBeenCalled()
      expect(notificationSpy).toHaveBeenCalledWith('Registrace proběhla úspěšně');
      expect(routerSpy).toHaveBeenCalledWith(['/login']);
    });

    it('should throw error', () => {
      const validFormValue = {email: "test@test.cz", password: "password"} as LoginForm
      component.formGroup = formBuilder.group(validFormValue)
      fixture.detectChanges()

      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');
      const authSpy = authService.register = jest.fn().mockReturnValue(throwError(() => ({status: 500})));

      component.onSubmit();

      expect(authSpy).toHaveBeenCalled();
      expect(notificationSpy).toHaveBeenCalledWith('Registrace se nezdařila');
    })

    it('invalid formGroup', () => {
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      component.onSubmit();

      expect(notificationSpy).toHaveBeenCalledWith('Formulář obsahuje nevalidní informace');
    })
  })

  describe('login form', () => {
    it('should open login form', () => {
      const routerSpy = jest.spyOn(router, 'navigate')

      fixture.ngZone?.run(() => {
        component.openLoginForm();
      })
      expect(routerSpy).toHaveBeenCalledWith(['/login']);
    })
  })

});
