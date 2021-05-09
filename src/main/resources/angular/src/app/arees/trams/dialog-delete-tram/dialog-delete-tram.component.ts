import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-delete-tram',
  templateUrl: './dialog-delete-tram.component.html',
  styleUrls: ['./dialog-delete-tram.component.scss']
})
export class DialogDeleteTramComponent implements OnInit {

  public data;
  constructor(private thisDialogRef: MatDialogRef<DialogDeleteTramComponent>, @Inject(MAT_DIALOG_DATA) data: any) {
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
