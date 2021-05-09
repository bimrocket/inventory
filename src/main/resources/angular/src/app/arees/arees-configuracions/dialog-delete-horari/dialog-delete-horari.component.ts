import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-delete-horari',
  templateUrl: './dialog-delete-horari.component.html',
  styleUrls: ['./dialog-delete-horari.component.scss']
})
export class DialogDeleteHorariComponent implements OnInit {

  public data;
  constructor(private thisDialogRef: MatDialogRef<DialogDeleteHorariComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
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
