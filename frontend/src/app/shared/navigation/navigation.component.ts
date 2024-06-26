import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  signal,
  WritableSignal
} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatToolbar} from "@angular/material/toolbar";
import {NavigationEnd, Router, RouterLink} from "@angular/router";
import {HttpService} from "../http/service/http.service";
import {AuthService} from "../../auth/service/auth.service";
import {NgIf} from "@angular/common";
import {filter, Subscription} from "rxjs";

@Component({
  selector: 'app-navigation',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    MatIcon,
    MatToolbar,
    RouterLink,
    NgIf
  ],
  providers: [
    Router,
    HttpService,
    AuthService
  ],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.css'
})
export class NavigationComponent implements OnInit, OnDestroy {
  protected isUserSignedIn: WritableSignal<boolean> = signal(false)
  private readonly subscriptions: Subscription[] = []
  private currentUrl: string = ''

  constructor(private router: Router,
              private authService: AuthService,
              private changeDetectorRef: ChangeDetectorRef) {
  }

  ngOnInit() {
    this.currentUrl = this.router.url
    this.isUserSignedIn.set(this.authService.isSignedIn())
    this.navigationRouter()
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((subscription) => subscription.unsubscribe())
  }

  isSelected(navigationUrl: string) {
    return this.currentUrl == navigationUrl
  }

  private navigationRouter() {
    this.subscriptions.push(this.router.events.pipe(filter((event) =>
      event instanceof NavigationEnd)
    ).subscribe((event) => {
      this.isUserSignedIn.set(this.authService.isSignedIn())
      this.currentUrl = (event as NavigationEnd).url
      this.changeDetectorRef.detectChanges()
    }));
  }

  signOut() {
    this.subscriptions.push(this.authService.signOut().subscribe(() => {
      window.location.reload();
    }))
  }
}
