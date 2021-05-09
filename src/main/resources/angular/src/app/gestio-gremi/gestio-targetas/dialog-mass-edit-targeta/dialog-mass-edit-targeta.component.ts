import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ToastService } from '../../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { Guild } from '../../../api/model/guild';
import { GuildMember } from '../../../api/model/guildMember';
import { GuildCard } from '../../../api/model/guildCard';
import { GuildMemberService } from '../../../api/api/guildMember.service';
import { FormControl, Validators } from '@angular/forms';
import { AuthorizationService } from 'app/services/authorization.service';
import { GuildCardStatus } from '../../../api/model/guildCardStatus';



@Component({
  selector: 'app-dialog-mass-edit-targeta',
  templateUrl: './dialog-mass-edit-targeta.component.html',
  styleUrls: ['./dialog-mass-edit-targeta.component.scss']
})
export class DialogMassEditTargetaComponent implements OnInit {

  
  public gremis: Guild[];
  public membres: GuildMember[];
  public cards: GuildCard[];
  public gremi: FormControl;
  public membre: FormControl;
  public membreAnterior: Number;
  public gremiAnterior: Number;
  public guildCardStates:Array<GuildCardStatus>;
  public guildCardState = new FormControl(undefined);
  public canviMembre: boolean;
  public canviGremi: boolean;


  constructor(private thisDialogRef: MatDialogRef<DialogMassEditTargetaComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private toast: ToastService, private guildMemberService: GuildMemberService, private translate: TranslateService, private auth: AuthorizationService) { 
		this.guildCardStates = data.states.filter(x => x.id == 4 || x.id == 5);
  
}

  ngOnInit() {
    this.cards = this.data.cards;
    this.gremis = this.data.gremis;
	this.canviMembre = false;
	this.canviGremi = false;
    this.gremi = new FormControl(undefined, [Validators.required]);
    this.membre = new FormControl(undefined, [Validators.required]);
    if(this.cards[0].guild !== null){
	  this.gremiAnterior = this.cards[0].guild.gremiId;
      this.gremi.setValue(this.cards[0].guild.gremiId);
      this.guildMemberService.guildMemberAllByGuild(this.cards[0].guild.gremiId, this.auth.getBearer())
        .subscribe((membres) => {
          this.membres = membres;
		  if(this.cards[0].guildMember !== null) {
			this.membre.setValue(this.cards[0].guildMember.memberId);
			this.membreAnterior = this.cards[0].guildMember.memberId;
			}
        })
    }
  
  }

  onCloseConfirm() {
    this.thisDialogRef.close({
      confirm : true,
      gremi : this.gremi.value,
	  membre : this.membre.value,
	  membreAnterior : this.membreAnterior,
	  gremiAnterior : this.gremiAnterior,
	  status:  this.guildCardState.value
    });
  }

  onCloseCancel() {
    this.thisDialogRef.close({
      confirm : false
    });
  }
/*
  public isAlreadyAssigned(): boolean {
    if (this.cards.guild != null) {
			 return true;
    } else {
      return false;
    }
  }*/


  gremiChanged(value) {
    if (this.gremi.value) {
      const selectedGremi = this.gremis.find((item) => {
        return item.gremiId == this.gremi.value;
      });
	  if(this.gremi.value == this.gremiAnterior) this.canviGremi = false;
	  else this.canviGremi = true;
      this.membre.setValue(undefined);
   /*   this.form.get('permissionsDUM').patchValue(selectedGremi.dumGranted);
      this.form.get('permissionsBSM').patchValue(selectedGremi.parkingBsmGranted);*/
      this.guildMemberService.guildMemberAllByGuild(selectedGremi.gremiId, this.auth.getBearer())
        .subscribe((membres) => {
          //this.membre.setValue(this.card.guildMember);
          this.membres = membres;
        })
    } else {
      if(this.gremiAnterior !== undefined) this.canviGremi = true;
	  else this.canviGremi = false;
      this.changeStateCard();
    }

  }


  agremiatChanged(value){
    if(!this.membre.value){
	    this.membre.setValue(undefined);
		if(this.membreAnterior == undefined) this.canviMembre = false;
	 	else this.canviMembre = true;
    }
	else {
		if( this.membreAnterior == undefined || this.membreAnterior !== this.membre.value) this.canviMembre = true;

		this.canviMembre = false;
	}
	
  }
	
	changeStateCard(){    
	    this.gremi.setValue(undefined);
	    this.membre.setValue(undefined);
	  }

	showCards(): String{
		var cadena = "";
		for(var card of this.cards){
			cadena = cadena + card.cardNumber + " "
		}
		return cadena;
	}

}
