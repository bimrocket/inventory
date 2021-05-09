import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Router } from '@angular/router';
import { config } from 'config/config';
import { NavigationEndInterceptor } from './services/navigationEndInterceptor.service';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(
    public navigation: NavigationEndInterceptor,
    private translate: TranslateService, private router: Router) {
    this.translate.setDefaultLang(config.LOCALE);
    this.translate.use(config.LOCALE);
    this.navigation.setup();
  }
}
