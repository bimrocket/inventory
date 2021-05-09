import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { DeleteGremiComponent } from '../../gremis/delete-gremi/delete-gremi.component';

@Component({
  selector: 'app-delete-permisos',
  templateUrl: './delete-permisos.component.html',
  styleUrls: ['./delete-permisos.component.scss']
})
export class DeletePermisosComponent implements OnInit {

  constructor(private thisDialogRef: MatDialogRef<DeleteGremiComponent>) { }
  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

}
