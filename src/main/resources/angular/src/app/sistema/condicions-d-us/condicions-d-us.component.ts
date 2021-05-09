import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { TermsService } from '../../api/api/terms.service';
import { ToastService } from '../../services/toast.service';
import { MatDialog } from '@angular/material';
import { DialogDeleteCondicionsComponent } from './dialog-delete-condicions/dialog-delete-condicions.component';
import { TranslateService } from '@ngx-translate/core';
import { ServiceCondition } from '../../api/model/serviceCondition';

@Component({
  selector: 'app-condicions-d-us',
  templateUrl: './condicions-d-us.component.html',
  styleUrls: ['./condicions-d-us.component.scss']
})
export class CondicionsDUsComponent implements OnInit {

  public allTerms: ServiceCondition[] = [];
  public loadingCondicions = true;
  constructor(
    private translate: TranslateService,
    private toast: ToastService, private dialog: MatDialog,
    private auth: AuthorizationService, private terms: TermsService) { 
  }

  ngOnInit() {
    this.loadTerms();
  }

  /**
   * Carrega la condicions d'ús
   * @returns void
   */
  private loadTerms(): void {
    this.terms.getAllServiceConditions().subscribe((terms: ServiceCondition[]) => {
      this.allTerms = terms;
      this.loadingCondicions = false;
    }, (err) => {
      this.toast.error(err);
      this.loadingCondicions = false;
    });
  }

  /**
   * Mostra el dialog de borrar una condició d'ús
   * @param  {any} termsId Id de la condició per eliminar
   * @returns void
   */
  public deleteTermsDialog(termsId: any): void {
    const dialogRef = this.dialog.open(DialogDeleteCondicionsComponent, {
      width: '600px',
      data: {}
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.terms.deleteServiceCondition(termsId).subscribe(() => {
          this.translate.get('TERMS.DELETED_OK').subscribe((message) => {
            this.toast.notify('success', message);
            this.loadTerms();
          });
        }, (fail) => {
          this.toast.error(fail);
        });
      }
    });
  }

}
