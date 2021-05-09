import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { CityConfigService } from '../../api/api/cityConfig.service';
import { CurrentUserService } from '../../services/current-user.service';
import { MatDialog } from '@angular/material';
import { DialogDeleteHorariComponent } from './dialog-delete-horari/dialog-delete-horari.component';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { ActivatedRoute } from '@angular/router';
import { ScheduleCityConf } from '../../api/model/scheduleCityConf';
import { DataService } from '../../api/api/data.service';
import { ZoneType } from '../../api';
import * as moment from 'moment';
import { SegmentsService } from '../../api/api/segments.service';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';

@Component({
  selector: 'app-arees-configuracions',
  templateUrl: './arees-configuracions.component.html',
  styleUrls: ['./arees-configuracions.component.scss']
})
export class AreesConfiguracionsComponent implements OnInit {

  public schedules: Array<ScheduleCityConf>;
  public zoneTypes: ZoneType[];
  public loadingSchedules = true;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    public currentUser: CurrentUserService,
    private data: DataService, private segmentService: SegmentsService,
    private curUser: CurrentUserService, private route: ActivatedRoute,
    private auth: AuthorizationService, private cityConf: CityConfigService,
    private dialog: MatDialog, private toast: ToastService, private translate: TranslateService) {

    this.loadData();
  }

  loadData() {
    this.loadingSchedules = true;
    this.cityConf.schedules(this.curUser.getMunicipi()).subscribe((schedules) => {
      this.schedules = schedules;
      this.loadingSchedules = false;
    }, (err) => {
      this.toast.error(err);
    });
  }

  deleteHorari(sche: ScheduleCityConf): void {
    const dialogRef = this.dialog.open(DialogDeleteHorariComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe((info) => {
      if (info === 'Confirm') {
        this.cityConf.deleteSchedule(this.curUser.getMunicipi(), sche.scheduleId).subscribe((ok) => {
          this.translate.get('AREES.CONFIGURACIONS.DELETED_CORRECTLY_SCHEDULE').subscribe((message) => {
            this.toast.notify('success', message);
            this.loadData();
          });
        }, (fail) => {
          this.toast.error(fail);
        });
      }
    })
  }

  ngOnInit() {
  }

}
