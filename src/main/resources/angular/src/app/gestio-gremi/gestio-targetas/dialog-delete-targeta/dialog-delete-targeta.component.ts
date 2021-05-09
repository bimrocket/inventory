import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { GuildCard } from '../../../api/model/guildCard';

@Component({
  selector: 'app-dialog-delete-targeta',
  templateUrl: './dialog-delete-targeta.component.html',
  styleUrls: ['./dialog-delete-targeta.component.scss']
})
export class DialogDeleteTargetaComponent implements OnInit {

  public card: GuildCard;

  constructor(private thisDialogRef: MatDialogRef<DialogDeleteTargetaComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { 
  }

  ngOnInit() {
    this.card = this.data.card;
  }

  onCloseConfirm() {
    this.thisDialogRef.close({
      confirm : true
    });
  }

  onCloseCancel() {
    this.thisDialogRef.close({
      confirm : false
    });
  }

}
