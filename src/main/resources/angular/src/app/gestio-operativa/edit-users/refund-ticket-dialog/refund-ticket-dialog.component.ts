import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { TicketsService } from '../../../api';
import { ToastService } from '../../../services/toast.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { TranslateService } from '@ngx-translate/core';
import { FormControl, Validators } from '@angular/forms';

@Component({
  selector: 'app-refund-ticket-dialog',
  templateUrl: './refund-ticket-dialog.component.html',
  styleUrls: ['./refund-ticket-dialog.component.scss']
})
export class RefundTicketDialogComponent implements OnInit {
  
  public reason: string;
  public amount: number;
  public reasonFC = new FormControl('', [Validators.required]);

  constructor(private thisDialogRef: MatDialogRef<RefundTicketDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private _toast: ToastService, private _auth: AuthorizationService,
  private _translate: TranslateService, private ticketService: TicketsService) {
  }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.ticketService.refundTicket(this._auth.getBearer(), {
      ticketReferenceId : this.data.ticketId,
      reason : this.reason,
      dateCreation : new Date().toISOString(),
      amount : this.amount
    }, 'response').subscribe((info) => {
      this._translate.get('REFUND.COMPLETE').subscribe((message) => {
        this._toast.notify('success', message);
      });
      this.thisDialogRef.close('Confirm');
    }, (err) => {
      this._toast.error(err);
    })
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
