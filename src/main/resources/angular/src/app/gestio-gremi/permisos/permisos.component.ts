import { Component, OnInit } from '@angular/core';
import { Form, FormGroup, FormControl, Validators } from '@angular/forms';
import { DeletePermisosComponent } from './delete-permisos/delete-permisos.component';
import { MatDialog } from '@angular/material';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { Permission } from '../../api/model/permission';
import { PermissionService } from '../../api/api/permission.service';
import { AuthorizationService } from '../../services/authorization.service';
import { DataService } from '../../api/api/data.service';
import { CityConfigService } from '../../api/api/cityConfig.service';
import { CurrentUserService } from '../../services/current-user.service';
import { ScheduleCityConf } from '../../api/model/scheduleCityConf';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';
import {DateAdapter} from '@angular/material';
@Component({
  selector: 'app-permisos',
  templateUrl: './permisos.component.html',
  styleUrls: ['./permisos.component.scss']
})
export class PermisosComponent implements OnInit {

  public permisos: Array<Permission>;
  public schedule: Array<ScheduleCityConf>;
  public municipis;
  public agreements;
  public periodicity;
  private lang: string;
  public loadingPermissions = false;
  public formFilter: FormGroup;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    private dialog: MatDialog, private toast: ToastService,
    private translate: TranslateService, private _permissionService: PermissionService,
    private auth: AuthorizationService, private data: DataService, private cityConf: CityConfigService,
    public curUser: CurrentUserService, private dateAdapter:DateAdapter<any>
  ) { }

  ngOnInit() {
    this.dateAdapter.getFirstDayOfWeek = () => { return 1; }
    this.loadingPermissions = true;
    this.lang = this.translate.getDefaultLang() == 'ca' ? 'cat' : this.translate.getDefaultLang();
    this.formFilter = new FormGroup({
      permis: new FormControl(''),
      periodicity: new FormControl('')
    });

    this.data.getCities(true).subscribe((cities) => {
      this.municipis = cities;
      this.data.agreements(this.lang, this.auth.getBearer()).subscribe((agreements) => {
        this.agreements = agreements;
        this.data.periodicity().subscribe((periodicity) => {
          this.periodicity = periodicity;
        });
        this.getPermission(null, null);
      }, (err) => {
        this.toast.error(err);
        this.loadingPermissions = false;
      });
    }, (err) => {
      this.toast.error(err);
      this.loadingPermissions = false;
    });
  }

  getPermission(namePermission, periodicityTypeId) {
    this._permissionService.permission(this.auth.getBearer(), namePermission, periodicityTypeId).subscribe((permis) => {
      this.permisos = permis;
      this.permisos.forEach((perm: Permission) => {
      })
      this.loadingPermissions = false;
    }, (err) => {
      this.toast.error(err);
      this.loadingPermissions = false;
    });
  }

  clearInputs() {
    this.formFilter.reset();
  }


  deletePermis(id: number): void {
    const dialogRef = this.dialog.open(DeletePermisosComponent, {
      width: '600px',
      data: {
        id: id
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.translate.get('PERMISOS.DELETED_CORRECTLY_RES').subscribe((message) => {
          this.toast.notify('success', message);
        });
        this._permissionService.deletePermission(id, this.auth.getBearer()).subscribe(() => {
          this.getPermission(null, null);
        }, (err) => {
          this.toast.error(err);
        });
      }
    });
  }

  save() {
    this.getPermission(this.formFilter.get('permis').value, this.formFilter.get('periodicity').value);
  }

  getTextPeriodicity(periodicityType) {
    return periodicityType[this.lang];
  }


}
