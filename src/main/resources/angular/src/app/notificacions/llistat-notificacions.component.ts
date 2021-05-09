import { Component, OnInit } from '@angular/core';
import { DataService } from '../api/api/data.service';
import { NotificationService } from '../api';
import { AuthorizationService } from '../services/authorization.service';
import { DeleteNotificacioDialogComponent } from './delete-notificacio-dialog/delete-notificacio-dialog.component';
import { MatDialog } from '@angular/material';
import { ToastService } from '../services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-llistat-notificacions',
  templateUrl: './llistat-notificacions.component.html',
  styleUrls: ['./llistat-notificacions.component.scss']
})
export class LlistatnotificacionsComponent implements OnInit {

  public nots: any;
  private notsTypes: any;
  public loadingNots = true;

  constructor(private _authService: AuthorizationService,
    private _dataService: DataService, private _notificationsService: NotificationService,
    private _dialog: MatDialog, private _toast: ToastService, private translate: TranslateService) {
    this.loadNotificacions();
    _dataService.notificationTypes(_authService.getBearer()).subscribe((data) => {
      this.notsTypes = data;
    });
  }

  deleteNotificacioDialog(id: any): void {
    const dialogRef = this._dialog.open(DeleteNotificacioDialogComponent, {
      width: '600px',
      data: {
        id: id
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this._notificationsService.deleteNotification(id, this._authService.getBearer()).subscribe(() => {
          this.translate.get('LLISTAT_NOTIFICACIONS.DELETED').subscribe((message) => {
            this._toast.notify('success', message);
          })
          this.loadNotificacions();
        }, (fail) => {
          this._toast.error(fail);
        });
      }
    });
  }

  loadNotificacions() {
    this.loadingNots = true;
    this._notificationsService.notificationsBO(this._authService.getBearer(), undefined).subscribe((data) => {
      this.nots = data;
      this.loadingNots = false;
    }, (error) => {
      this._toast.error(error);
      this.loadingNots = false;
    });
  }

  ngOnInit() {
  }

}
