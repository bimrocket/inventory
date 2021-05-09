import { Component, OnInit } from '@angular/core';
import { RestrictionConf } from '../../api/model/restrictionConf';
import { AutofocusDirective } from 'ngx-material-timepicker/src/app/material-timepicker/directives/autofocus.directive';
import { AuthorizationService } from '../../services/authorization.service';
import { CityConfigService } from '../../api/api/cityConfig.service';
import { CurrentUserService } from '../../services/current-user.service';
import { MatDialog } from '@angular/material';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';
import { DialogDeleteResComponent } from '../restricions/dialog-delete-res/dialog-delete-res.component';
import { config } from 'config/config';
import { Router } from '@angular/router';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';

@Component({
  selector: 'app-restricions',
  templateUrl: './restricions.component.html',
  styleUrls: ['./restricions.component.scss']
})
export class RestricionsComponent implements OnInit {

  public res: Array<RestrictionConf>;
  public loadingRes = false;
  public FUNCIONALITIES = FUNCIONALITIES;
  constructor(
    private router: Router,
    private auth: AuthorizationService, private cityConf: CityConfigService,
    public curUser: CurrentUserService, private dialog: MatDialog,
    private translate: TranslateService, private toast: ToastService
  ) { }

  ngOnInit() {
    if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
      this.router.navigateByUrl('/inici');
    }
    this.loadData();
  }

  loadData() {
    this.loadingRes = true;
    this.cityConf.restrictions(this.curUser.getMunicipi()).subscribe((res) => {
      this.res = res;
      this.loadingRes = false;
    });
  }

  deleteRes(res: RestrictionConf) {
    const dialogRef = this.dialog.open(DialogDeleteResComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe((info) => {
      if (info === 'Confirm') {
        this.cityConf.deleteRestriction(this.curUser.getMunicipi(), res.restrictionId).subscribe((ok) => {
          this.translate.get('AREES.RESTRICIONS.DELETED_CORRECTLY_RES').subscribe((message) => {
            this.toast.notify('success', message);
            this.loadData();
          });
        }, (fail) => {
          this.toast.error(fail);
        });
      }
    })
  }
}
