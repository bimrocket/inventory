import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-historic-transaction',
  templateUrl: './dialog-historic-transaction.component.html',
  styleUrls: ['./dialog-historic-transaction.component.scss']
})
export class DialogHistoricTransactionComponent implements OnInit {

  public data;
  constructor(private thisDialogRef: MatDialogRef<DialogHistoricTransactionComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
    this.data = data;
    this.data.history.sort((a, b) => {
      const aHistoryDate = new Date(a.historyDate);
      const bHistoryDate = new Date(b.historyDate);
      if (aHistoryDate > bHistoryDate) {
        return 1;
      } else if (aHistoryDate < bHistoryDate) {
        return -1;
      } else {
        return 0;
      }
    });
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
