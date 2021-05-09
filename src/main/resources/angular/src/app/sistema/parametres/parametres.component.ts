import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { ConfigurationService } from '../../api';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-parametres',
  templateUrl: './parametres.component.html',
  styleUrls: ['./parametres.component.scss']
})
export class ParametresComponent implements OnInit {

  private configs: any;
  public families: any = [];
  public loadingParams = true;

  constructor(
    public toast: ToastService, public translate: TranslateService,
    private auth: AuthorizationService, private configuration: ConfigurationService) {
    this.configuration.configurations(1, this.auth.getBearer()).subscribe((configs) => {
      this.configs = configs;
      this.configs.forEach(config => {
        const family = this.findFamily(config.family);
        if (config.value === 'false') {
          config.value = false;
        }
        if (config.value === 'true') {
          config.value = true;
        }
        family.items.push(config);
      });
      this.families.forEach(family => {
        family.items.sort((a,b) => {
          if (a.description < b.description) {
            return -1;
          } else if (a.description > b.description) {
            return 1;
          } else {
            return 0;
          }
        });
      });
      this.loadingParams = false;
    }, (error) => {
      this.toast.error(error);
      this.loadingParams = false;
    });
  }

  onInputChange(item) {
    item.modified = true;
  }

  toggleValue(item) {
    item.value = !item.value;
    item.modified = true;
  }

  saveParams() {
    this.families.forEach(family => {
      family.items.forEach(item => {
        if (item.modified) {
          delete item.modified;
          this.configuration.updateConfigurations(1, this.auth.getBearer(), item).subscribe((ok) => {
            this.translate.get('PARAMS.OK').subscribe((message) => {
              this.toast.notify('success', message);
            });
          }, (error) => {
            this.toast.error(error);
          });
        }
      })
    });
  }

  findFamily(family) {
    let found = false;
    let indexFound = 0;
    this.families.forEach((element, index) => {
      if (element.value === family) {
        found = true;
        indexFound = index;
      }
    });
    if (found) {
      return this.families[indexFound];
    } else {
      this.families.push({
        value : family,
        name : 'PARAMS.FAMILY_' + family.toUpperCase(),
        items : []
      });
      return this.families[this.families.length - 1];
    }
  }

  ngOnInit() {
  }

}
