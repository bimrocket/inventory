import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-dialog-delete-condicions',
  templateUrl: './dialog-delete-condicions.component.html',
  styleUrls: ['./dialog-delete-condicions.component.scss']
})
export class DialogDeleteCondicionsComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DialogDeleteCondicionsComponent>) { }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
