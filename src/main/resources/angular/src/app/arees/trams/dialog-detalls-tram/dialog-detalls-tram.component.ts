import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { CityConfigService } from '../../../api/api/cityConfig.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { ConfigurationConf } from '../../../api/model/configurationConf';
import { RateCityConf } from '../../../api/model/rateCityConf';
import { ScheduleCityConf } from '../../../api/model/scheduleCityConf';
import { DataService } from '../../../api/api/data.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { ToastService } from '../../../services/toast.service';

@Component({
  selector: 'app-dialog-detalls-tram',
  templateUrl: './dialog-detalls-tram.component.html',
  styleUrls: ['./dialog-detalls-tram.component.scss']
})
export class DialogDetallsTramComponent implements OnInit {

  public data;
  public config: ConfigurationConf;
  public rate: RateCityConf;
  public schedule: ScheduleCityConf;
  public loadingDetalls = true;
  constructor(
    private toast: ToastService,
    private dataService: DataService, private auth: AuthorizationService,
    private cityConf: CityConfigService, private curUser: CurrentUserService,
    private thisDialogRef: MatDialogRef<DialogDetallsTramComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
      this.data = data;

        this.cityConf.schedule(this.curUser.getMunicipi(), this.data.tram.get('scheduleId')).subscribe((schedule) => {
          this.schedule = schedule;
          this.loadingDetalls = false;
        }, (err) => {
          this.toast.error(err);
        });
  }

  getZoneTypeName(zoneTypeId: number) {
    if (zoneTypeId === undefined) {
      return 'AREES.TRAMS.INVALID_CONFIG';
    } else {
      return this.data.zoneTypes.find((item) => {
        return item.zoneTypeId === zoneTypeId;
      }).description.cat;
    }
  }

  ngOnInit() {
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
