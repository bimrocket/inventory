import { Component, OnInit } from '@angular/core';
import { ToastService } from '../../services/toast.service';
import { UserboService } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { MatDialog } from '@angular/material';
import { DataService } from '../../api/api/data.service';
import { CurrentUserService } from '../../services/current-user.service';
import { WatcherService } from '../../api/api/watcher.service';
import { DeleteDialogUsuariBoComponent } from '../../sistema/usuaris-bo/delete-dialog-usuari-bo/delete-dialog-usuari-bo.component';
import { TranslateService } from '@ngx-translate/core';
import { DialogDeleteVIgilantsComponent } from './dialog-delete-vigilants/dialog-delete-vigilants.component';
import { OauthService } from './../../api/api/oauth.service';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';

@Component({
  selector: 'app-gestio-vigilants',
  templateUrl: './gestio-vigilants.component.html',
  styleUrls: ['./gestio-vigilants.component.scss']
})
export class GestioVigilantsComponent implements OnInit {

  operacioBorrar = 'borrar';
  selectedOperacio = this.operacioBorrar;
  search = '';
  thirdUsers = [];
  watchers = [];
  selected = [];
  cities: any = [];
  loadingWatcher = true;
  public FUNCIONALITIES = FUNCIONALITIES;


  constructor(
    private toast: ToastService,
    private watcher: WatcherService, private translate: TranslateService,
    private _dialog: MatDialog, private _dataService: DataService,
    private _oauthService: OauthService,
    public curUserService: CurrentUserService) {
    this.loadUsers();
  }

  loadUsers() {
    this.loadingWatcher = true;
    this.watcher.watchers().subscribe((watchers) => {
      this.watchers = watchers;
      this.loadingWatcher = false;
    }, (err) => {
      this.toast.error(err);
      this.loadingWatcher = false;
    })
  }

  deleteUserDialog(users: any): void {
    const dialogRef = this._dialog.open(DialogDeleteVIgilantsComponent, {
      width: '600px',
      data: {
        users: users
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.watcher.deleteWatcher(users[0].watcherId).subscribe((ok) => {
          this.translate.get('VIGILANTS.DELETED').subscribe((message) => {
            this.toast.notify('success', message);
            this.loadUsers();
          });
        }, (err) => {
          this.toast.error(err);
        })
      }
    });
  }

  sendEmailWatcher(watcher) {
    if (watcher.length > 0) {
      this._oauthService.resetPasswordInfo(watcher[0].email, 'watcher').subscribe(
        (data: any) => {
          this.translate.get('VIGILANTS.EMAIL_SENT').subscribe((message) => {
            this.toast.notify('success', message);
          });
        },
        (err) => {
          this.toast.error(err);
        }
      );
    }
  }

  getCityNameById(cityId) {
    let found = false;
    let i = 0;
    while (!found && i !== this.cities.length) {
      if (this.cities[i].juridicId === cityId) {
        found = true;
      } else {
        i++;
      }
    }
    return found ? this.cities[i].name : 'GENERAL.NONE';
  }

  isSelected(userBo: any) {
    return this.selected.indexOf(userBo) !== -1;
  }

  isAllSelected() {
    return this.selected.length === this.watchers.length;
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
      this.watchers.forEach((element) => {
        this.selected.push(element);
      });
    }
  }

  ngOnInit() {
  }


}
