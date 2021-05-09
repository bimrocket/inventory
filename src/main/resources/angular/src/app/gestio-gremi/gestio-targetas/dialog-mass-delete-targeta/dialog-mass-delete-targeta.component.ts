import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { GuildCard } from '../../../api/model/guildCard';

@Component({
  selector: 'app-dialog-mass-delete-targeta',
  templateUrl: './dialog-mass-delete-targeta.component.html',
  styleUrls: ['./dialog-mass-delete-targeta.component.scss']
})

export class DialogMassDeleteTargetaComponent implements OnInit {

  public cards: GuildCard[];

  constructor(private thisDialogRef: MatDialogRef<DialogMassDeleteTargetaComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { 
  }

  ngOnInit() {
    this.cards = this.data.cards;
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

	showCards(): String{
		var cadena = "";
		for(var card of this.cards){
			cadena = cadena + card.cardNumber + " "
		}
		return cadena;
	}


}
