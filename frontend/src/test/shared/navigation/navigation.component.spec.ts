import {NavigationComponent} from "../../../app/shared/navigation/navigation.component";
import {ComponentFixture, TestBed} from "@angular/core/testing";
import {NavigationEnd, Router} from "@angular/router";
import {HttpService} from "../../../app/shared/http/service/http.service";
import {AuthService} from "../../../app/auth/service/auth.service";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {BehaviorSubject} from "rxjs";
import {RouterTestingModule} from "@angular/router/testing";

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavigationComponent, RouterTestingModule, HttpClientTestingModule],
      providers: [
        Router,
        HttpService,
        AuthService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set currentUrl on initialization', () => {
    expect(component['currentUrl']).toBe('/');
  });

  it('should set isUserSignedIn on initialization', () => {
    expect(component['isUserSignedIn']()).toBeFalsy();
  });

  it('should update currentUrl on router events', () => {
    const navigationEnd = new NavigationEnd(1, '/new-url', '/new-url');
    const routerEventsSubject = new BehaviorSubject<any>(navigationEnd);

    jest.spyOn(router, 'events', 'get').mockReturnValue(routerEventsSubject.asObservable());

    component.ngOnInit();

    expect(component['currentUrl']).toBe('/new-url');
  });

  it('should return true when currentUrl matches navigationUrl', () => {
    component['currentUrl'] = '/some-url';
    expect(component.isSelected('/some-url')).toBeTruthy();
  });

  it('should return false when currentUrl does not match navigationUrl', () => {
    component['currentUrl'] = '/some-url';
    expect(component.isSelected('/other-url')).toBeFalsy();
  });
});
