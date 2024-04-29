import {TestBed} from "@angular/core/testing";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {AuthService} from "../../app/auth/service/auth.service";
import {HttpService} from "../../app/shared/http/service/http.service";
import {SignInForm} from "../../app/auth/model/SignInForm";
import {SignUpForm} from "../../app/auth/model/SignUpForm";
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

  it('should call httpService.postWithOptions for sign in', () => {
    const signInForm: SignInForm = {email: 'test@test.cz', password: 'password'};
    httpService.postWithOptions = jest.fn().mockReturnValue(of({}));

    authService.signIn(signInForm).subscribe(response => {
      expect(response.status).toBe(200);
      expect(sessionStorage.getItem('auth')).toBe('true');
    });

    expect(httpService.postWithOptions).toHaveBeenCalled()
  });

  it('should call httpService.post for sign up', () => {
    const signUpForm: SignUpForm = {
      username: 'test',
      email: 'test@example.com',
      password: 'password',
      passwordConfirmation: 'password'
    };
    httpService.post = jest.fn().mockReturnValue(of({}));

    authService.signUp(signUpForm).subscribe();

    expect(httpService.post).toHaveBeenCalled();
  });
});
