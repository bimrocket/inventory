import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../../services/authorization.service';
import { CityConfigService } from '../../../api/api/cityConfig.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';
import { CurrentUserService } from '../../../services/current-user.service';
import { ScheduleCityConf } from '../../../api/model/scheduleCityConf';
import { ToastService } from '../../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material';
import { DIalogSelectHolidaysComponent } from '../dialog-select-holidays/dialog-select-holidays.component';
import { VacationDay } from '../../../api/model/vacationDay';

import * as moment from 'moment';

export interface Day {
  name: string,
  from1?: string,
  to1?: string,
  from2?: string,
  to2?: string,
  from3?: string,
  to3?: string
};

export const HORARI_DAYS: Array<Day> = [
  {
    name: 'AREES.HORARIS.MONDAY'
  },
  {
    name: 'AREES.HORARIS.TUESDAY'
  },
  {
    name: 'AREES.HORARIS.WEDNESDAY'
  },
  {
    name: 'AREES.HORARIS.THURSDAY'
  },
  {
    name: 'AREES.HORARIS.FRIDAY'
  },
  {
    name: 'AREES.HORARIS.SATURDAY'
  },
  {
    name: 'AREES.HORARIS.SUNDAY'
  }
];
export const HORARI_DATES_START: Array<any> = [
  'mondayStart',
  'tuesdayStart',
  'wednesdayStart',
  'thursdayStart',
  'fridayStart',
  'saturdayStart',
  'sundayStart'
];
export const HORARI_DATES_END: Array<any> = [
  'mondayEnd',
  'tuesdayEnd',
  'wednesdayEnd',
  'thursdayEnd',
  'fridayEnd',
  'saturdayEnd',
  'sundayEnd',
];
@Component({
  selector: 'app-add-horari',
  templateUrl: './add-edit-horari.component.html',
  styleUrls: ['./add-edit-horari.component.scss']
})
export class AddEditHorariComponent implements OnInit {

  public days: Array<Day> = HORARI_DAYS;
  public params;
  public addingNew = false;
  public desc = new FormControl('', [Validators.required]);
  public descCA = new FormControl('', [Validators.required]);
  public descES = new FormControl('', [Validators.required]);
  public descEN = new FormControl('', [Validators.required]);
  public code = new FormControl('', [Validators.required]);
  public datesStart = HORARI_DATES_START;
  public datesEnd = HORARI_DATES_END;
  public schedule: ScheduleCityConf;
  public holidayDays: Array<moment.Moment> = [];

  constructor(
    private dialog: MatDialog,
    private auth: AuthorizationService, private cityConf: CityConfigService,
    private route: ActivatedRoute, private curUser: CurrentUserService,
    private toast: ToastService, private translate: TranslateService,
    private router: Router) {
  }

  selectHolidays() {
    const dialogRef = this.dialog.open(DIalogSelectHolidaysComponent, {
      width: '600px',
      data: {
        days: this.holidayDays,
        from: moment().subtract(5, 'years').toDate(),
        to: moment().add(100, 'years').toDate()
      }
    });


    dialogRef.afterClosed().subscribe((data) => {
      if (data.days !== undefined) {
        this.holidayDays = data.days;
      }
    })
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.params = params;
      this.descEN.patchValue('none');
      if (this.params.id === undefined) {
        this.addingNew = true;

        for (let i = 0; i < this.datesStart.length; i++) {
          this.days[i].from1 = null;
          this.days[i].from2 = null;
          this.days[i].from3 = null;
          this.days[i].to1 = null;
          this.days[i].to2 = null;
          this.days[i].to3 = null;
        }
      } else {
        this.cityConf.schedule(this.curUser.getMunicipi(), this.params.id).subscribe((schedule) => {
          this.schedule = schedule;
          this.desc.patchValue(schedule.description);
          this.code.patchValue(schedule.scheduleCode);
          this.descCA.patchValue(schedule.description_i18n.cat);
          this.descES.patchValue(schedule.description_i18n.es);
          this.descEN.patchValue(schedule.description_i18n.en);
          if (this.isDisabled()) {
            this.desc.disable();
            this.code.disable();
            this.descCA.disable();
            this.descES.disable();
            this.descEN.disable();
          }
          for (let i = 0; i < this.datesStart.length; i++) {
            if (schedule[this.datesStart[i]][0]) {
              this.days[i].from1 = this.getTime(schedule[this.datesStart[i]][0]);
            } else {
              this.days[i].from1 = null;
            }
            if (schedule[this.datesStart[i]][1]) {
              this.days[i].from2 = this.getTime(schedule[this.datesStart[i]][1]);
            } else {
              this.days[i].from2 = null;
            }
            if (schedule[this.datesStart[i]][2]) {
              this.days[i].from3 = this.getTime(schedule[this.datesStart[i]][2]);
            } else {
              this.days[i].from3 = null;
            }
            if (schedule[this.datesEnd[i]][0]) {
              this.days[i].to1 = this.getTime(schedule[this.datesEnd[i]][0]);
            } else {
              this.days[i].to1 = null;
            }
            if (schedule[this.datesEnd[i]][1]) {
              this.days[i].to2 = this.getTime(schedule[this.datesEnd[i]][1]);
            } else {
              this.days[i].to2 = null;
            }
            if (schedule[this.datesEnd[i]][2]) {
              this.days[i].to3 = this.getTime(schedule[this.datesEnd[i]][2]);
            } else {
              this.days[i].to3 = null;
            }
          }
          this.schedule.vacationDay.forEach((elem) => {
            this.holidayDays.push(moment(elem.vacationDay));
          });
        });
      }
    });
  }

  isDisabled() {
    if (this.addingNew) {
      return false;
    } else if (this.schedule) {
      return this.schedule.deletedDate;
    }
  }

  saveHorari() {
    let schedule: ScheduleCityConf;
    schedule = this.addingNew ? {
      scheduleId: -1,
      scheduleCode: '',
      descriptionI18n: {
        cat: '',
        es: '',
        en: ''
      }
    } : this.schedule;
    schedule.description = this.desc.value;
    schedule.scheduleCode = this.code.value;
    schedule.description_i18n = {
      cat: this.descCA.value,
      es: this.descES.value,
      en: this.descEN.value
    };
    for (let i = 0; i < this.datesStart.length; i++) {
      schedule[this.datesStart[i]] = [];
      schedule[this.datesStart[i]][0] = this.getDate(this.days[i].from1);
      schedule[this.datesStart[i]][1] = this.getDate(this.days[i].from2);
      schedule[this.datesStart[i]][2] = this.getDate(this.days[i].from3);
    }
    for (let i = 0; i < this.datesEnd.length; i++) {
      schedule[this.datesEnd[i]] = [];
      schedule[this.datesEnd[i]][0] = this.getDate(this.days[i].to1);
      schedule[this.datesEnd[i]][1] = this.getDate(this.days[i].to2);
      schedule[this.datesEnd[i]][2] = this.getDate(this.days[i].to3);
    }
    if (this.addingNew) {
      const vacations: Array<VacationDay> = [];
      if (this.holidayDays) {
        this.holidayDays.forEach((vac) => {
          const vacation: VacationDay = {
            vacationId: 0,
            vacationDay: vac.toISOString()
          }
          vacations.push(vacation);
        })
      }
      schedule.vacationDay = vacations;
      this.cityConf.createSchedule(this.curUser.getMunicipi(), schedule).subscribe((ok) => {
        this.translate.get('AREES.HORARIS.SAVED_CORRECTLY').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/arees/configuracions']);
        })
      }, (fail) => {
        this.toast.error(fail);
      });
    } else {
      if (this.holidayDays) {
        const vacations: Array<VacationDay> = [];
        this.holidayDays.forEach((vac) => {
          const foundVac = schedule.vacationDay.filter((configVac) => {
            return moment(configVac.vacationDay).isSame(vac, 'day');
          });
          const vacation: VacationDay = {
            vacationId: foundVac[0] ? foundVac[0].vacationId : 0,
            vacationDay: vac.toISOString()
          }
          vacations.push(vacation);
        });
        schedule.vacationDay = vacations;
      }
      this.cityConf.editSchedule(this.curUser.getMunicipi(), this.params.id, schedule).subscribe((ok) => {
        this.translate.get('AREES.HORARIS.SAVED_CORRECTLY').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/arees/configuracions']);
        })
      }, (fail) => {
        this.toast.error(fail);
      });
    }
  }

  getTime(date: string) {
    const date2 = new Date(date);
    return date2.getHours() + ':' + date2.getMinutes();
  }

  getDate(time) {
    const newDate = new Date();
    if (time && time !== '' && time.length > 1) {
      const timeSplited = time.split(':');
      newDate.setHours(timeSplited[0]);
      newDate.setMinutes(timeSplited[1]);
      newDate.setSeconds(0);
      newDate.setMilliseconds(0);
      return newDate.toISOString();
    } else {
      return undefined;
    }
  }

}
