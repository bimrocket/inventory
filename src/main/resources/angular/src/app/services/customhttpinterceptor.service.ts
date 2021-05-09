// src/app/auth/token.interceptor.ts
import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { AuthorizationService } from './authorization.service';
import { from } from 'rxjs';
import { UserService } from '../api';
import { Router } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import { environment } from 'environments/environment';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  private observable: any;
  constructor(public auth: AuthorizationService, public userService: UserService,public router: Router) { }

  applyAuthHeader(request: HttpRequest<any>): HttpRequest<any> {
    if ((request.headers.get('Authorization') == null || request.headers.get('Authorization') === '')
    && this.auth.getAccessToken() != null && this.auth.getAccessToken() !== 'false' && this.auth.getAccessToken()) {
      request = request.clone({
        setHeaders: {
          Authorization: this.auth.getBearer()
        }
      });
    }
    return request;
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const observable = Observable.create((observer) => {
      next.handle(this.applyAuthHeader(request)).toPromise().then((res) => {
        observer.next(res);
        observer.complete();
      }).catch((res) => {
        if (res.status === 401) {
          if (this.auth.getRefreshToken() != null) {
            this.userService.loginUser(environment.clientId, 'refresh_token',
            'Basic ' + btoa(environment.clientId + ':' + environment.clientSecret), this.auth.getRefreshToken(), null, null)
            .subscribe((data) => {
              const body = JSON.parse(data._body);
              this.auth.setAccessToken(body.access_token);
              this.auth.setRefreshToken(body.refresh_token);
              request = request.clone({
                setHeaders : {
                  Authorization : ''
                }
              });
              next.handle(this.applyAuthHeader(request)).toPromise().then(function(res2) {
                observer.next(res2);
                observer.complete();
              }).catch((err) => {
                if (err.status === 401) {
                  this.router.navigate(['/login']);
                }
                console.error(err);
                observer.error(err);
              });
            }, (err) => {
              this.router.navigate(['/login']);
              console.error(err);
              observer.error(err);
            }, () => {
            });
          } else {
            this.router.navigate(['/login']);
            observer.error('no refresh token');
          }

        } else {
          observer.error(res);
        }
      });
    });

    return observable;
  }
}
