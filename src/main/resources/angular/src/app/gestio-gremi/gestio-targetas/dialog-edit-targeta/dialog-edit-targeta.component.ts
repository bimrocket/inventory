import { Component, OnInit, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { GuildCard} from '../../../api/model/guildCard';
import { GuildCardStatus } from '../../../api/model/guildCardStatus';
import { GuildCardsService } from '../../../api/api/guildCards.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'app-dialog-edit-targeta',
  templateUrl: './dialog-edit-targeta.component.html',
  styleUrls: ['./dialog-edit-targeta.component.scss']
})
export class DialogEditTargetaComponent implements OnInit {

  public targeta:GuildCard;
  public guildCardState = new FormControl(undefined);
  public statusPlaceholder:Number;
  public guildCardStates:Array<GuildCardStatus>;
  public guildCardStatesAux:Array<GuildCardStatus>;

  constructor(
	public thisDialogRef: MatDialogRef<DialogEditTargetaComponent>, 
	@Inject(MAT_DIALOG_DATA) private data: any) {
		this.targeta = data.targeta;
		this.guildCardStates = data.states.filter(x => x.id == 4 || x.id == 5);
		this.guildCardStatesAux = data.states.filter(x => x.id == 5);
		let reactivateState = {} as GuildCardStatus; 
		reactivateState.id = this.targeta.previousDisabledStatus || 8; 
		reactivateState.text = 'Reactivar';
		this.guildCardStatesAux.push(reactivateState);
 }

  ngOnInit() {
    if (this.targeta.guildCardStatusId) {
      this.guildCardState.patchValue(this.targeta.guildCardStatusId);
	}
	
	this.guildCardState.valueChanges.subscribe(valor => {
		console.log('change')
		console.log(valor)
		//this.guildCardState.setValue(valor);
		//this.targeta.guildCardStatusId = valor
	})

	
}
  

 public onCloseConfirm(): void {
    this.thisDialogRef.close({
      confirm : true,
      status:  this.guildCardState.value
    });
  }

  public onCloseCancel(): void {
    this.thisDialogRef.close({
      confirm : false
    });
  }
}
