import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserService } from '../../../api';
import { ToastService } from '../../../services/toast.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-detall-dialog-component',
  templateUrl: './detall-dialog-component.component.html',
  styleUrls: ['./detall-dialog-component.component.scss']
})
export class DetallDialogComponentComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DetallDialogComponentComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private _userService: UserService, private _toast: ToastService, private _auth: AuthorizationService,
  private _translate: TranslateService) { 
  }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.data.user.companyName = this.data.user.account.companyName;
    this._userService.updateUser(this.data.user.userId, this._auth.getBearer(), this.data.user, 'response').subscribe((resp) => {
      if (resp.status === 200) {
        this._translate.get('EDIT_USUARIS.PERMIS_CHANGED').subscribe((message) => {
          this._toast.notify('success', message);
        });
      } else {
        this._translate.get('EDIT_USUARIS.PERMIS_FAILED').subscribe((message) => {
          this._toast.notify('danger', message);
        });
      }
    });
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

  toggleIsAdmin() {
    this.data.user.permissions.isAdmin = !this.data.user.permissions.isAdmin;
  }

  toggleFine() {
    this.data.user.permissions.fineAnnulation = !this.data.user.permissions.fineAnnulation;
  }

  toggleCC() {
    this.data.user.permissions.setCreditCard = !this.data.user.permissions.setCreditCard;
  }

  toggleVehicles() {
    this.data.user.permissions.addVehicles = !this.data.user.permissions.addVehicles;
  }
}
