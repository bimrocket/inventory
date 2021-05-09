import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-delete-vehicles',
  templateUrl: './delete-vehicles.component.html',
  styleUrls: ['./delete-vehicles.component.scss']
})
export class DeleteVehiclesComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DeleteVehiclesComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
