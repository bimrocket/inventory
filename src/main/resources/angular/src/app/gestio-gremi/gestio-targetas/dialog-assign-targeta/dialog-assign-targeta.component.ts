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


@Component({
  selector: 'app-dialog-assign-targeta',
  templateUrl: './dialog-assign-targeta.component.html',
  styleUrls: ['./dialog-assign-targeta.component.scss']
})
export class DialogAssignTargetaComponent implements OnInit {

 
  public gremis: Guild[];
  public membres: GuildMember[];
  public card: GuildCard;
  public gremi: FormControl;
  public membre: FormControl;
  public membreAnterior: Number;
  public gremiAnterior: Number;

  constructor(private thisDialogRef: MatDialogRef<DialogAssignTargetaComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
  private toast: ToastService, private guildMemberService: GuildMemberService, private translate: TranslateService, private auth: AuthorizationService) { 
  }

  ngOnInit() {
    this.card = this.data.card;
    this.gremis = this.data.gremis;
    this.gremi = new FormControl(undefined, [Validators.required]);
    this.membre = new FormControl(undefined, [Validators.required]);
    if(this.card.guild !== null){
	  this.gremiAnterior = this.card.guild.gremiId;
      this.gremi.setValue(this.card.guild.gremiId);
      this.guildMemberService.guildMemberAllByGuild(this.card.guild.gremiId, this.auth.getBearer())
        .subscribe((membres) => {
          this.membres = membres;
		  if(this.card.guildMember !== null) {
			this.membre.setValue(this.card.guildMember.memberId);
			this.membreAnterior = this.card.guildMember.memberId;
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
	  gremiAnterior : this.gremiAnterior
    });
  }

  onCloseCancel() {
    this.thisDialogRef.close({
      confirm : false
    });
  }

  public isAlreadyAssigned(): boolean {
    if (this.card.guild != null) {
			 return true;
    } else {
      return false;
    }
  }


  gremiChanged(value) {
    if (this.gremi.value) {
      const selectedGremi = this.gremis.find((item) => {
        return item.gremiId == this.gremi.value;
      });
      this.membre.setValue(undefined);
   /*   this.form.get('permissionsDUM').patchValue(selectedGremi.dumGranted);
      this.form.get('permissionsBSM').patchValue(selectedGremi.parkingBsmGranted);*/
      this.guildMemberService.guildMemberAllByGuild(selectedGremi.gremiId, this.auth.getBearer())
        .subscribe((membres) => {
          //this.membre.setValue(this.card.guildMember);
          this.membres = membres;
        })
    } else {
      this.changeStateCard();
    }

  }


  agremiatChanged(value){
    if(!this.membre.value){
	    this.membre.setValue(undefined);
    }
  }
	
	changeStateCard(){    
	    this.gremi.setValue(undefined);
	    this.membre.setValue(undefined);
	  }


}
