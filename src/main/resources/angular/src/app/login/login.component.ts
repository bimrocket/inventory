import { Component, OnInit } from '@angular/core';

import { FormControl, Validators } from '@angular/forms';
import { UserService } from '../api';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthorizationService } from '../services/authorization.service';
import { CurrentUserService } from '../services/current-user.service';
import { ToastService } from '../services/toast.service';
import { OauthService } from '../api/api/oauth.service';
import { environment } from 'environments/environment';
import { DomSanitizer } from '@angular/platform-browser';
import { OAuthSSOInfo } from '../api/model/oAuthSSOInfo';
import { TranslateService } from '@ngx-translate/core';

declare var $: any;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  public email = new FormControl('', [Validators.required]);
  public password = new FormControl('', [Validators.required]);
  public remember_me = { name: 'remember_me', checked: false };
  public showError = false;
  public iframeSrc;
  public loadingLogin = true;


  constructor(
    private activatedRoute: ActivatedRoute, private translate: TranslateService,
    private oauth: OauthService, private dom: DomSanitizer,
    private toast: ToastService, private _currentUserService: CurrentUserService,
    private _userService: UserService, private _router: Router, private _authService: AuthorizationService) {
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const sso = params['sso'];
      if (sso === 'ok') {
        this._userService.loginUser(environment.clientId, 'password',
        'Basic ' + btoa(environment.clientId + ':' + environment.clientSecret), null, params['userLdap'], params['token'])
        .subscribe(
          (data) => {
            let body = JSON.parse(data._body);
            this._authService.setAccessToken(body.access_token);
            this._authService.setRefreshToken(body.refresh_token);
            this._currentUserService.loadUser();
            this._router.navigate(['/inici']);
            this.translate.get('LOGIN.SSO_VALID').subscribe((message) => {
              this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
            });
            this.loadingLogin = false;
          },
          (err) => {
            this.translate.get('LOGIN.SSO_INVALID').subscribe((message) => {
              this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
            });
            this.loadingLogin = false;
          }
        );
      } else if (sso === 'invalid') {
        this.translate.get('LOGIN.SSO_INVALID').subscribe((message) => {
          this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
        });
        this.loadingLogin = false;
      } else if (sso === 'notFound') {
        this.translate.get('LOGIN.SSO_NOT_FOUND').subscribe((message) => {
          this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
        });
        this.loadingLogin = false;
      } else {
        this.loadingLogin = false;
      }
    });
  }

  login() {
    this._userService.loginUser(environment.clientId, 'password',
      'Basic ' + btoa(environment.clientId + ':' + environment.clientSecret), null, this.email.value, this.password.value)
      .subscribe(
        (data) => {
          let body = JSON.parse(data._body);
          this._authService.setAccessToken(body.access_token);
          this._authService.setRefreshToken(body.refresh_token);
          this._currentUserService.loadUser();
          this._router.navigate(['/inici']);
        },
        (err) => {
          this.showError = true;
          this.toast.error(err);
          setTimeout(() => {
            this.showError = false;
          }, 3000);
        },
        () => {
        }
      );
  }

  onChange(event, item) {
    item.checked = !item.checked;
  }

  getEmailErrorMessage() {
    return this.email.hasError('required') ? 'LOGIN.YOU_NEED_VALUE' :
      this.email.hasError('email') ? 'LOGIN.INVALID_EMAIL' :
        '';
  }

  getPassErrorMessage() {
    return this.email.hasError('required') ? 'LOGIN.YOU_NEED_VALUE' :
      '';
  }

}
