import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-delete-gremi',
  templateUrl: './delete-gremi.component.html',
  styleUrls: ['./delete-gremi.component.scss']
})
export class DeleteGremiComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DeleteGremiComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }
  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}

