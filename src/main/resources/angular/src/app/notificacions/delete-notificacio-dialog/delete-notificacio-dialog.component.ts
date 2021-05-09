import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-delete-notificacio-dialog',
  templateUrl: './delete-notificacio-dialog.component.html',
  styleUrls: ['./delete-notificacio-dialog.component.scss']
})
export class DeleteNotificacioDialogComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DeleteNotificacioDialogComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
