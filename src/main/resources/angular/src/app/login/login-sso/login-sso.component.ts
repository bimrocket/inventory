import { Component, OnInit } from '@angular/core';
import { environment } from 'environments/environment';

@Component({
  selector: 'app-login-sso',
  templateUrl: './login-sso.component.html',
  styleUrls: ['./login-sso.component.scss']
})
export class LoginSSOComponent implements OnInit {

  constructor() {
  }

  ngOnInit() {
    window.location.href = environment.api
    + '/oauth/sso?redirect='
    + window.location.href.substring(0 , window.location.href.length - 4);
  }

}
