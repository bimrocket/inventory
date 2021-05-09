import { Component, OnInit } from '@angular/core';
import { UserboService, UserService, ResetUserPasswordRequest, UserBO, JuridicEntity } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { MatDialog, MatTabBodyOriginState } from '@angular/material';
import { DeleteDialogUsuariBoComponent } from './delete-dialog-usuari-bo/delete-dialog-usuari-bo.component';
import { DataService } from '../../api/api/data.service';
import { CurrentUserService } from '../../services/current-user.service';
import { ToastService } from '../../services/toast.service';
import { config } from 'config/config';
import { TranslateService } from '@ngx-translate/core';
import { FUNCIONALITIES, PROFILES } from '../../globalVariables/globalVariables';
import { RolesService } from 'app/services/roles.service';

@Component({
  selector: 'app-usuaris-bo',
  templateUrl: './usuaris-bo.component.html',
  styleUrls: ['./usuaris-bo.component.scss']
})
export class UsuarisBoComponent implements OnInit {

  public operacioBorrar = 'borrar';
  public selectedOperacio = this.operacioBorrar;
  public search = undefined;
  public thirdUsers = [];
  public usersbo: UserBO[] = [];
  public selected = [];
  public cities: JuridicEntity[];
  public loadingUserBo = true;
  public FUNCIONALITIES = FUNCIONALITIES;


  constructor(
    private oauth: UserService, private rolesService: RolesService,
    private toast: ToastService, private translate: TranslateService,
    private _usersBoService: UserboService, private _auth: AuthorizationService,
    private _dialog: MatDialog, private _dataService: DataService, public _currentUserService: CurrentUserService) {
    this.loadUsers();
  }

  recoverPassword(userbo: UserBO) {
    this.oauth.resetPasswordInfo(userbo.email, config.CLIENT_ID, false, this._auth.getBearer()).subscribe(() => {
      this.translate.get('USUARIS_BO.RECOVER_OK').subscribe((message) => {
        this.toast.notify('success', message);
      });
    }, (fail) => {
      this.toast.error(fail);
    });
  }

  loadUsers() {
    this.loadingUserBo = true;
    this._usersBoService.getAllUsersBo().subscribe((data) => {
      this.usersbo = data;
      // Filtrem usuaris por rols accessibles
      this.usersbo = this.usersbo.filter((userBo) => {
        return this.rolesService.isRolAccessible(this._currentUserService.getProfile(), userBo.profile.description);
      })
      this._dataService.getCities(false).subscribe((cities) => {
        this.cities = cities;
        this.usersbo.forEach((userBo) => {
          userBo.city = this.getCities(userBo.cities);
        });

      });
      this.loadingUserBo = false;
    }, (err) => {
      this.toast.error(err);
      this.loadingUserBo = false;
    });
  }

  deleteUserDialog(users: any): void {
    const dialogRef = this._dialog.open(DeleteDialogUsuariBoComponent, {
      width: '600px',
      data: {
        users: users
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.loadUsers();
      }
    });
  }

  getCities(cityIds: number[]) {
    let cities = '';
    let i = 0;
    if (cityIds != null && cityIds.length > 0) {
      while (i !== this.cities.length) {
        for (let index = 0; index < cityIds.length; index++) {
          const element = cityIds[index];
          if (this.cities[i].juridicId === cityIds[index]) {
            cities += this.cities[i].name + ', ';
          }
        }
        i++;
      }
    }
    if (cities !== '') {
      cities = cities.substring(0, cities.length - 2);
    } else {
      cities = 'GENERAL.ALL';
    }
    return cities;
  }

  isSelected(userBo: any) {
    return this.selected.indexOf(userBo) !== -1;
  }

  isAllSelected() {
    return this.selected.length === this.usersbo.length;
  }

  toggleUser(userBo: any) {
    if (this.isSelected(userBo)) {
      this.selected.splice(this.selected.indexOf(userBo), 1);
    } else {
      this.selected.push(userBo);
    }


  }

  applyOperacio() {
    if (this.selectedOperacio === this.operacioBorrar) {
      this.deleteUserDialog(this.selected);
    }
  }

  toggleAll() {
    if (this.isAllSelected()) {
      this.selected = [];
    } else {
      this.selected = [];
      this.usersbo.forEach((element) => {
        this.selected.push(element);
      });
    }
  }

  ngOnInit() {
  }

}
