import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-stop-ticket',
  templateUrl: './dialog-stop-ticket.component.html',
  styleUrls: ['./dialog-stop-ticket.component.scss']
})
export class DIalogStopTicketComponent implements OnInit {


  public data;
  constructor(private thisDialogRef: MatDialogRef<DIalogStopTicketComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
    this.data = data;
  }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }


}
