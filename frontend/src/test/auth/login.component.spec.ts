import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LoginComponent} from '../../app/auth/login/login.component';
import {AuthService} from "../../app/auth/service/auth.service";
import {FormBuilder} from "@angular/forms";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {Router} from "@angular/router";
import {of, throwError} from "rxjs";
import {
  FrontendNotificationService
} from "../../app/shared/frontend-notification/service/frontend-notification.service";
import {LoginForm} from "../../app/auth/model/LoginForm";

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let notificationService: FrontendNotificationService;
  let authService: AuthService
  let router: Router;
  let formBuilder: FormBuilder;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, HttpClientTestingModule, BrowserAnimationsModule],
      providers: [
        AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
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

  describe('login', () => {
    it('should login', () => {
      const validFormValue = {email: "test@test.cz", password: "password"} as LoginForm
      component.formGroup = formBuilder.group(validFormValue)
      fixture.detectChanges()

      authService.login = jest.fn().mockReturnValue(of({}));
      const notificationSpy = jest.spyOn(notificationService, 'successNotification');
      const routerSpy = jest.spyOn(router, 'navigate')

      fixture.ngZone?.run(() => {
        component.onSubmit();
      })

      expect(authService.login).toHaveBeenCalled()
      expect(notificationSpy).toHaveBeenCalledWith('Ůspěšně jste se přihlásili');
      expect(routerSpy).toHaveBeenCalledWith(['/']);
    });

    it('invalid password', () => {
      const validFormValue = {email: "test@test.cz", password: "password"} as LoginForm
      component.formGroup = formBuilder.group(validFormValue)
      fixture.detectChanges()

      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');
      const authSpy = authService.login = jest.fn().mockReturnValue(throwError(() => ({status: 500})));

      component.onSubmit();

      expect(authSpy).toHaveBeenCalled();
      expect(notificationSpy).toHaveBeenCalledWith('Špatné heslo nebo email');
    })

    it('invalid formGroup', () => {
      const notificationSpy = jest.spyOn(notificationService, 'errorNotification');

      component.onSubmit();

      expect(notificationSpy).toHaveBeenCalledWith('Formulář obsahuje nevalidní informace');
    })
  })

  describe('register form', () => {
    it('should call router.navigate on register', () => {
      const routerSpy = jest.spyOn(router, 'navigate')

      fixture.ngZone?.run(() => {
        component.openRegistrationForm();
      })
      expect(routerSpy).toHaveBeenCalledWith(['/registration']);
    })
  })

});
