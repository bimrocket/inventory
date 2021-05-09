import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { TermsService } from '../../api/api/terms.service';
import { MatDialog } from '@angular/material';
import { DialogDeletePPComponent } from './dialog-delete-pp/dialog-delete-pp.component';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-politiques-p',
  templateUrl: './politiques-p.component.html',
  styleUrls: ['./politiques-p.component.scss']
})
export class PolitiquesPComponent implements OnInit {

  public pp: any;
  public loadingPP = true;

  constructor(
    private _auth: AuthorizationService, private terms: TermsService,
    private dialog: MatDialog, private translate: TranslateService, 
    private toast: ToastService) {
    this.loadPP();
  }

  loadPP() {
    this.loadingPP = true;
    this.terms.getAllPrivacyPolices().subscribe((pp) => {
      this.pp = pp;
      this.loadingPP = false;
    }, (err) => {
      this.toast.error(err);
      this.loadingPP = false;
    })
  }

  deletePPDialog(ppId: any): void {
    const dialogRef = this.dialog.open(DialogDeletePPComponent, {
      width: '600px',
      data: {}
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.terms.deletePrivacyPolicy(ppId).subscribe(() => {
          this.translate.get('PP.DELETED_OK').subscribe((message) => {
            this.toast.notify('success', message);
            this.loadPP();
          });
        }, (fail) => {
          this.toast.error(fail);
        }, () => {
        });
      }
    });
  }

  ngOnInit() {
  }

}
