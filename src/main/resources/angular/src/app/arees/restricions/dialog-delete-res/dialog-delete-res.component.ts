import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-delete-res',
  templateUrl: './dialog-delete-res.component.html',
  styleUrls: ['./dialog-delete-res.component.scss']
})
export class DialogDeleteResComponent implements OnInit {

  public data;
  constructor(private thisDialogRef: MatDialogRef<DialogDeleteResComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
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
