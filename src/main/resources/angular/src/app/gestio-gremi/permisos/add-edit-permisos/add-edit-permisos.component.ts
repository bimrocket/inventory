import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, Route } from '@angular/router';
import { FormControl, Validators,FormGroup  } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../services/toast.service';
import { PermissionService } from '../../../api/api/permission.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { Permission } from '../../../api/model/permission';
import { DataService } from '../../../api/api/data.service';
import {PeriodicityType} from '../../../api/model/periodicityType';
import { ScheduleCityConf } from '../../../api/model/scheduleCityConf';
import {CityConfigService} from '../../../api/api/cityConfig.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { MatSlideToggleChange } from '@angular/material';
import {
  HORARI_DAYS,
  HORARI_DATES_START,
  HORARI_DATES_END
} from '../../../arees/arees-configuracions/add-edit-horari/add-edit-horari.component';

@Component({
  selector: 'app-add-edit-permisos',
  templateUrl: './add-edit-permisos.component.html',
  styleUrls: ['./add-edit-permisos.component.scss']
})
export class AddEditPermisosComponent implements OnInit {

  public params: any;
  public days = HORARI_DAYS;
  public datesStart = HORARI_DATES_START;
  public datesEnd = HORARI_DATES_END;
  private lang:string;
  public municipis;
  public agreements;
  public periodicity:PeriodicityType[];
  public schedule: ScheduleCityConf[];
  public exist: Boolean;

  form = new FormGroup({
    permisNom: new FormControl('', Validators.required),
    permisQuantitat: new FormControl('', Validators.required),
    permisVigencia: new FormControl('', Validators.required),
    permisPeriodicitat: new FormControl('', Validators.required),
    permisMunicipi: new FormControl('', Validators.required),
    permisTime:new FormControl('',Validators.required),
    permisAgreement:new FormControl('',Validators.required),
    permisIllimitat : new FormControl(false)
  }); 

  loadPermission: boolean = true;
  error: any = {};
  public permissions: Permission;
  public permissionSend: Permission;


  constructor(
    private toast: ToastService, private router: Router,
    private translate: TranslateService, private route: ActivatedRoute,
    private permissionService: PermissionService, private auth: AuthorizationService,
    private data: DataService,private cityConf: CityConfigService, private curUser: CurrentUserService) {

  }

  ngOnInit() {
    this.lang = this.translate.getDefaultLang() == 'ca'?'cat':this.translate.getDefaultLang() ;
    this.data.getCities(true).subscribe((cities) => {
      this.municipis = cities;
    });
    this.data.agreements(this.lang,this.auth.getBearer()).subscribe((agreements)=>{
      this.agreements = agreements;
    })
    this.data.periodicity().subscribe((periodicity)=>{
      this.periodicity = periodicity;
    });

    this.route.params.subscribe(params => {
      this.params = params;
      this.exist = false;
      if (this.params.id != 'new') {
        this.exist = true;
        this.permissionService.getPermission(this.params.id,this.auth.getBearer()).subscribe((permission)=>{
          this.permissions = permission;
          this.form.get('permisNom').setValue(this.permissions.name);
          this.form.get('permisQuantitat').setValue(this.permissions.quantity);
          this.form.get('permisVigencia').setValue(this.permissions.expirationDate);
          this.form.get('permisPeriodicitat').setValue(this.permissions.periodicityId);
          this.form.get('permisMunicipi').setValue(this.permissions.cityId);
          this.form.get('permisTime').setValue(this.permissions.timeInMinutes);
          this.form.get('permisAgreement').setValue(this.permissions.agreementType);
          this.loadPermission=false;
          for (let i = 0; i < this.datesStart.length; i++) {
            if (permission.schedule[this.datesStart[i]][0]) {
              this.days[i].from1 = this.getTime(permission.schedule[this.datesStart[i]][0]);
            } else {
              this.days[i].from1 = null;
            }

            if (permission.schedule[this.datesStart[i]][1]) {
              this.days[i].from2 = this.getTime(permission.schedule[this.datesStart[i]][1]);
            } else {
              this.days[i].from2 = null;
            }

            if (permission.schedule[this.datesStart[i]][2]) {
              this.days[i].from3 = this.getTime(permission.schedule[this.datesStart[i]][2]);
            } else {
              this.days[i].from3 = null;
            }

            if (permission.schedule[this.datesEnd[i]][0]) {
              this.days[i].to1 = this.getTime(permission.schedule[this.datesEnd[i]][0]);
            } else {
              this.days[i].to1 = null;
            }

            if (permission.schedule[this.datesEnd[i]][1]) {
              this.days[i].to2 = this.getTime(permission.schedule[this.datesEnd[i]][1]);
            } else {
              this.days[i].to2 = null;
            }

            if (permission.schedule[this.datesEnd[i]][2]) {
              this.days[i].to3 = this.getTime(permission.schedule[this.datesEnd[i]][2]);
            } else {
              this.days[i].to3 = null;
            }

          }
        })
      }else{
        this.loadPermission=false;

        for (let i = 0; i < this.datesStart.length; i++) {
              this.days[i].from1 = null;
              this.days[i].from2 = null;
              this.days[i].from3 = null;
              this.days[i].to1 = null;
              this.days[i].to2 = null;
              this.days[i].to3 = null;
        }
      }
    });

  }

  save(): void {
    if (this.form.status == 'INVALID') {
      this.translate.get('error_167').subscribe((message) => {
        this.error.errorCode = message;
        this.toast.showerror(this.error);
      });
      this.error.errorCode = null;
      return;
    };

    this.permissionSend = {
      permissionId: this.params.id == "new"?0:this.params.id,
      name: this.form.get('permisNom').value, 
      quantity: this.form.get('permisQuantitat').value,
      periodicityId: this.form.get('permisPeriodicitat').value,
      expirationDate: this.form.get('permisVigencia').value,
      cityId: this.form.get('permisMunicipi').value,
      timeInMinutes: this.form.get('permisTime').value,
      agreementType: this.form.get('permisAgreement').value,
      schedule : {
        permissionId: -1
      }
    };

    

    for (let i = 0; i < this.datesStart.length; i++) {
      this.permissionSend.schedule[this.datesStart[i]] = [];
      this.permissionSend.schedule[this.datesStart[i]][0] = this.getDate(this.days[i].from1);
      this.permissionSend.schedule[this.datesStart[i]][1] = this.getDate(this.days[i].from2);
      this.permissionSend.schedule[this.datesStart[i]][2] = this.getDate(this.days[i].from3);
    }
    for (let i = 0; i < this.datesEnd.length; i++) {
      this.permissionSend.schedule[this.datesEnd[i]] = [];
      this.permissionSend.schedule[this.datesEnd[i]][0] = this.getDate(this.days[i].to1);
      this.permissionSend.schedule[this.datesEnd[i]][1] = this.getDate(this.days[i].to2);
      this.permissionSend.schedule[this.datesEnd[i]][2] = this.getDate(this.days[i].to3);
    }


    if(this.params.id == "new"){
      this.permissionService.createPermission(this.auth.getBearer(),this.permissionSend).subscribe((permission)=>{
        this.permissions = permission;
        this.translate.get('PERMISSOS.CREATED').subscribe((message) => {
          this.toast.notify('success', message);
        });
        this.router.navigateByUrl('/gestio-gremi/permisos');
       
      }, (err) => {
        this.toast.error(err);
      });
    }else{
      this.permissionService.savePermission(this.params.id,this.auth.getBearer(),this.permissionSend).subscribe((permission)=>{
        this.translate.get('PERMISSOS.UPDATED').subscribe((message) => {
          this.toast.notify('success', message);
        });
        this.router.navigateByUrl('/gestio-gremi/permisos')

      }, (err) => {
        this.toast.error(err);
      });
    }

  }

  isDisabled() {
    return false;
  }

  descriptionPermission(periode:PeriodicityType){
    return periode.description[this.lang];
  }

  descriptionSchedule(schedule:ScheduleCityConf){
    return schedule.description_i18n[this.lang];
  }

  public toggleQuantity($event: MatSlideToggleChange) {
    if ($event.checked) {
      this.form.get('permisQuantitat').patchValue(-1);
    } else {
      this.form.get('permisQuantitat').patchValue(10);
    }
  }

  getTime(date: string) {
    const date2 = new Date(date);
    return date2.getUTCHours() + ':' + date2.getUTCMinutes();
  }

  getDate(time) {
    const newDate = new Date();
    if (time && time !== '' && time.length > 1) {
      const timeSplited = time.split(':');
      newDate.setUTCHours(timeSplited[0]);
      newDate.setUTCMinutes(timeSplited[1]);
      newDate.setUTCSeconds(0);
      newDate.setUTCMilliseconds(0);
      return newDate.toISOString();
    } else {
      return undefined;
    }
  }

}
