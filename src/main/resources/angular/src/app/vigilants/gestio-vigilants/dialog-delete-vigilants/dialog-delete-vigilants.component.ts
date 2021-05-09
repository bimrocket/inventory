import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-dialog-delete-vigilants',
  templateUrl: './dialog-delete-vigilants.component.html',
  styleUrls: ['./dialog-delete-vigilants.component.scss']
})
export class DialogDeleteVIgilantsComponent implements OnInit {

  constructor(
    private thisDialogRef: MatDialogRef<DialogDeleteVIgilantsComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }
}
