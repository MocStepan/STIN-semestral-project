import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "../../app/auth/service/auth.service";
import {HttpService} from "../../app/shared/http/service/http.service";
import {LoginForm} from "../../app/auth/model/LoginForm";
import {RegistrationForm} from "../../app/auth/model/RegistrationForm";
import {of} from "rxjs";

describe('AuthService', () => {
  let authService: AuthService;
  let httpService: HttpService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService, HttpService]
    });

    authService = TestBed.inject(AuthService);
    httpService = TestBed.inject(HttpService)
  });

  it('should call httpService.postWithOptions for login', () => {
    const loginForm: LoginForm = {email: 'test@test.cz', password: 'password'};
    httpService.postWithOptions = jest.fn().mockReturnValue(of({}));

    authService.login(loginForm);

    expect(httpService.postWithOptions).toHaveBeenCalled()
  });

  it('should call httpService.get for logout', () => {
    httpService.get = jest.fn().mockReturnValue(of({}));

    authService.logout();

    expect(httpService.get).toHaveBeenCalled();
  });

  it('should call httpService.post for register', () => {
    const registrationForm: RegistrationForm = {
      username: 'test',
      email: 'test@example.com',
      password: 'password',
      passwordConfirmation: 'password'
    };
    httpService.post = jest.fn().mockReturnValue(of({}));

    authService.register(registrationForm).subscribe();

    expect(httpService.post).toHaveBeenCalled();
  });
});
