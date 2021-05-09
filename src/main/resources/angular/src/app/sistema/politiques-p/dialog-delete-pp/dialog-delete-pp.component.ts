import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Data } from '@angular/router';

@Component({
  selector: 'app-dialog-delete-pp',
  templateUrl: './dialog-delete-pp.component.html',
  styleUrls: ['./dialog-delete-pp.component.scss']
})
export class DialogDeletePPComponent implements OnInit {
  
  constructor(private thisDialogRef: MatDialogRef<DialogDeletePPComponent>) { }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
