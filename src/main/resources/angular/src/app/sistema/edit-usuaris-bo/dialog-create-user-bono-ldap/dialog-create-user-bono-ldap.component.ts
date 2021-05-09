import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

export interface Remitente {
  id: number,
  cc: string
}
@Component({
  selector: 'app-dialog-create-user-bono-ldap',
  templateUrl: './dialog-create-user-bono-ldap.component.html',
  styleUrls: ['./dialog-create-user-bono-ldap.component.scss']
})
export class DialogCreateUserBONoLDAPComponent implements OnInit {

  public idRem = 1;
  public remitents: Array<Remitente> = [
    {
      id : this.idRem++,
      cc : undefined
    }
  ];
  constructor(private thisDialogRef: MatDialogRef<DialogCreateUserBONoLDAPComponent>) {
  }

  ngOnInit() {
  }

  onCloseConfirm() {
    this.thisDialogRef.close(this.remitents);
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

  public addRemitente() {
    this.remitents.push({
      id : this.idRem++,
      cc : undefined
    });
  }

}
