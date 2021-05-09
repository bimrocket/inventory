import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'app-delete-agremiats',
  templateUrl: './delete-agremiats.component.html',
  styleUrls: ['./delete-agremiats.component.scss']
})
export class DeleteAgremiatsComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DeleteAgremiatsComponent>) { }
  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
