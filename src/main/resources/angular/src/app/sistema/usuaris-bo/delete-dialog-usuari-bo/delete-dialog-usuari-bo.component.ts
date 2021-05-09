import { Component, OnInit, Inject } from '@angular/core';

import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserService, UserboService } from '../../../api';
import { ToastService } from '../../../services/toast.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-delete-dialog-usuari-bo',
  templateUrl: './delete-dialog-usuari-bo.component.html',
  styleUrls: ['./delete-dialog-usuari-bo.component.scss']
})
export class DeleteDialogUsuariBoComponent implements OnInit {

  constructor(
    private thisDialogRef: MatDialogRef<DeleteDialogUsuariBoComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
    private _userBoService: UserboService, private _toast: ToastService, private _auth: AuthorizationService,
    private _translate: TranslateService) { }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.data.users.forEach(element => {
      this._userBoService.deleteUserBo(element.boUserId, this._auth.getBearer()).subscribe(() => {
        this._translate.get('USUARIS_BO.DELETE_CORRECTLY').subscribe((translated) => {
          this._toast.notify('success', translated);
        });
      }, (error) => {
        this._toast.error(error);
      });
    });
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }
}
