import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as moment from 'moment';

@Component({
  selector: 'app-dialog-select-holidays',
  templateUrl: './dialog-select-holidays.component.html',
  styleUrls: ['./dialog-select-holidays.component.scss']
})
export class DIalogSelectHolidaysComponent implements OnInit {

  public data;
  public from;
  public to;
  public myDays = [ ];
  public myMonth;
  constructor(private thisDialogRef: MatDialogRef<DIalogSelectHolidaysComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
    this.data = data;
    this.from = moment(data.from);
    this.to = moment(data.to);
    this.myMonth = moment().clone().locale('ca-ES').startOf('month');
    this.myDays = data.days;
  }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close({
      days : this.myDays
    });
  }

  onCloseCancel() {
    this.thisDialogRef.close({
      days : undefined
    });
  }


}
