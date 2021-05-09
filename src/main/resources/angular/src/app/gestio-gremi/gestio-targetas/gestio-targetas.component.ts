import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthorizationService } from '../../services/authorization.service';
import { GuildService } from '../../api/api/guild.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { Guild } from 'app/api/model/guild';
import { GuildCard } from '../../api/model/guildCard';
import { GuildMember } from '../../api/model/guildMember';
import { GuildCardStatus } from '../../api/model/guildCardStatus';
import { GuildCardCreate } from '../../api/model/guildCardCreate';
import { GuildCardsService } from '../../api/api/guildCards.service';
import { GuildMemberService } from '../../api/api/guildMember.service';
import { MatDialog, MatDialogRef } from '@angular/material';
import { DialogAssignTargetaComponent } from './dialog-assign-targeta/dialog-assign-targeta.component';
import { DialogDeleteTargetaComponent } from './dialog-delete-targeta/dialog-delete-targeta.component';
import { DialogEditTargetaComponent } from './dialog-edit-targeta/dialog-edit-targeta.component';
import { DialogMassEditTargetaComponent } from './dialog-mass-edit-targeta/dialog-mass-edit-targeta.component';
import { DialogMassDeleteTargetaComponent } from './dialog-mass-delete-targeta/dialog-mass-delete-targeta.component';

import { GremisFiltre } from '../gremis/gremis.component';
import { config } from 'config/config';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';
import { CurrentUserService } from '../../services/current-user.service';
import {DateAdapter} from '@angular/material';

export interface CardsFilter {
  guildId: number,
  cardNumber: number,
  cardNumberFrom: number,
  cardNumberTo: number,
  guildMemberName: string,
  guildMemberDni: string,
  dateCreatedFrom: Date,
  dateCreatedTo: Date,
  dateAssignedGuildFrom: Date,
  dateAssignedGuildTo: Date,
  dateAssignedGuildMemberFrom: Date,
  dateAssignedGuildMemberTo: Date,
  assignedToGuild: boolean,
  assignedToGuildMember: boolean,
  guildCardStatusId: number
}

@Component({
  selector: 'app-gestio-targetas',
  templateUrl: './gestio-targetas.component.html',
  styleUrls: ['./gestio-targetas.component.scss']
})
export class GestioTargetasComponent implements OnInit {

  public formCreate: FormGroup;
  public formSearch: FormGroup;
  public gremis: Array<Guild>;
  public savingTargetas = false;
  public cards: Array<GuildCard>;
  public loadingCards = false;
  public members: Array<GuildMember>;
  public states: Array<GuildCardStatus>;
  public exportInProgress = false;
  public showingResults = false;
  public FUNCIONALITIES = FUNCIONALITIES;
  public errorCreatedCard = null; 
  public allowMassEdition = false;
  public updated = false;

  constructor(
    private dialog: MatDialog, public curUser: CurrentUserService,
    private _guildService: GuildService, private translate: TranslateService,
    private toast: ToastService, private auth: AuthorizationService,
    private _guildCardService: GuildCardsService, private _guildMemberService: GuildMemberService, 
    private dateAdapter:DateAdapter<any>) {
    this.loadGremi();
	this.loadStates();
  }

  ngOnInit() {
    this.dateAdapter.getFirstDayOfWeek = () => { return 1; }

    this.formCreate = new FormGroup({
      numFrom: new FormControl('', Validators.required),
      numTo: new FormControl('', Validators.required),
      saldo: new FormControl(50, Validators.required),
      assign: new FormControl('')
    });

    this.formSearch = new FormGroup({
      nameGremi: new FormControl(undefined),
      numTargeta: new FormControl(undefined),
      numTargetaFrom: new FormControl(undefined),
      numTargetaTo: new FormControl(undefined),
      nomAgremiat: new FormControl(undefined),
      dniAgremiat: new FormControl(undefined),
      dateCreatedFrom: new FormControl(undefined),
      dateCreatedTo: new FormControl(undefined),
      dateAssignGremiFrom: new FormControl(undefined),
      dateAssignGremiTo: new FormControl(undefined),
      dateAssignAgremiatFrom: new FormControl(undefined),
      dateAssignAgremiatTo: new FormControl(undefined),
	  guildCardStatusId: new FormControl(undefined),

      assignedToGremi: new FormControl(null),
      assignedToAgremiat: new FormControl(null)
    });

  }


  /**
   * Limpia els valors posats
   * en formulari de cerca 'formSearch'
   * @returns void
   */
  public clearInputs(): void {
    this.formSearch.reset();
  }

  /**
   * 1. Mostra dialog d'assignació de targeta a un Gremi
   * 2. En cas confirmatiu assigna la targeta al gremi
   * 3. Si hi ha hagut error en assignació mostra el error
   * 4. Si no mostra notificació que s'ha assignat la targeta al grem
   * @param  {GuildCard} card
   * @returns void
   */
  public editarTargeta(card: GuildCard): void {
    const dialogRef = this.dialog.open(DialogAssignTargetaComponent, {
      width: '600px',
      data: {
        card: card,
        gremis: this.gremis,
	    auth : this.auth
      }
    })

    dialogRef.afterClosed().subscribe((data) => {
      if (data && data.confirm === true) {
			this._guildCardService.guildCardsIdAssignPut(card.cardId, this.auth.getBearer(), data.membre, data.gremi).subscribe((ok) => {
			 if(data.gremi !== undefined && data.gremiAnterior !== undefined && data.gremi == data.gremiAnterior){
				if(data.membre == undefined && data.membreAnterior !== undefined){
				this.translate.get('TARGETAS.UNASSIGNED_MEMBER', {
	            numTargeta: card.cardNumber,
	            membre: data.membreAnterior
	          }).subscribe((message) => {
	            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
	            this.search();
	          });
			   }
			}
			else if(data.gremi !== undefined && data.gremiAnterior == undefined){
				this.translate.get('TARGETAS.ASSIGNED', {
	            numTargeta: card.cardNumber,
	            gremi: data.gremi
	          }).subscribe((message) => {
	            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
	            this.search();
	          });
			}
			else {
				this.translate.get('TARGETAS.UNASSIGNED', {
	            numTargeta: card.cardNumber,
	            gremi: data.gremi
	          }).subscribe((message) => {
	            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
	            this.search();
	          });
			}
			if(data.membre !== undefined) {
	          this.translate.get('TARGETAS.ASSIGNED_MEMBER', {
	            numTargeta: card.cardNumber,
	            membre: data.membre
	          }).subscribe((message) => {
	            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
	            this.search();
	          });
			}
	        }, (err) => {
	          this.toast.error(err);
	        });
		}
    })
  }

  /**
   * Cerca les targetas amb filtre actual 'CardFilter'
   * obtingut de formulari de cerca 'formSearch'
   * @returns void
   */
  public search(): void {
    this.loadingCards = true;
    const filter: CardsFilter = this.getActualSearchFilter();
    this._guildCardService.guildCardsGet(this.auth.getBearer(),
      filter.guildId,
      filter.cardNumber,
      filter.cardNumberFrom,
      filter.cardNumberTo,
      filter.guildMemberName,
      filter.guildMemberDni,
      filter.dateCreatedFrom,
      filter.dateCreatedTo,
      filter.dateAssignedGuildFrom,
      filter.dateAssignedGuildTo,
      filter.dateAssignedGuildMemberFrom,
      filter.dateAssignedGuildMemberTo,
	  filter.guildCardStatusId,
      filter.assignedToGuild,
      filter.assignedToGuildMember
	  
    ).subscribe((cards) => {
      this.cards = cards;
      this.loadingCards = false;
      this.showingResults = true;
    }, (err) => {
      this.toast.error(err);
      this.loadingCards = false;
    });
  }

  /**
* Exporta targetas a excel amb filtres actuals
* 1. Bloqueja el botó de exportar
* 2. Notifica al usuari sobre export
* 3. Fa la petició al servidor per fer export
* 4. Notifica si ha hagut error sino descarrega el fitxer
* 5. Desbloqueja el botó de exportació
* @returns void
*/
  public exportTargetas(): void {
    this.exportInProgress = true;
    const filter: CardsFilter = this.getActualSearchFilter();
    this.translate.get('GENERAL.EXPORT_IN_PROGRESS').subscribe((message) => {
      this.toast.notify('warning', message);
      this._guildCardService.guildCardsXlsGet(this.auth.getBearer(),
        filter.guildId,
        filter.cardNumber,
        filter.cardNumberFrom,
        filter.cardNumberTo,
        filter.guildMemberName,
        filter.guildMemberDni,
        filter.dateCreatedFrom,
        filter.dateCreatedTo,
        filter.dateAssignedGuildFrom,
        filter.dateAssignedGuildTo,
        filter.dateAssignedGuildMemberFrom,
        filter.dateAssignedGuildMemberTo,
        filter.assignedToGuild,
        filter.assignedToGuildMember,
        config.LOCALE)
        .subscribe((file) => {
          saveAs(file, 'Extracte targetas del ' + new Date().toLocaleDateString() + '.xls');
          this.exportInProgress = false;
        }, (err) => {
          this.toast.error(err);
          this.exportInProgress = false;
        })

    });


  }

  /**
   * 1. Mostra dialog de eliminar targeta 
   * 2. Si el resultat es confirm elimina la targeta
   * 3. En cas d'error al eliminar mostra error
   * 4. Notifica eliminació correcte de targeta
   * @param  {GuildCard} card Targeta a eliminar
   * @returns void
   */
  public deleteTargeta(card: GuildCard): void {
    const dialogRef = this.dialog.open(DialogDeleteTargetaComponent, {
      width: '600px',
      data: {
        card: card
      }
    });

    dialogRef.afterClosed().subscribe((data) => {
      if (data && data.confirm === true) {
        this._guildCardService.guildCardsIdDelete(card.cardId, this.auth.getBearer()).subscribe((ok) => {
          this.translate.get('TARGETAS.DELETED', {
            numTargeta: card.cardNumber
          }).subscribe((message) => {
            this.search();
            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
          });
        }, (err) => {
          this.toast.error(err);
        });
      }
    })
  }
  

  /**
   * Crea les targetas amb valors del formulari formCreate
   * @returns void
   */
  public save(): void {
    this.errorCreatedCard = null;
    if (this.formCreate.status === 'INVALID') {
      return;
    } else {
      this.savingTargetas = true;
      const guildCardCreated: GuildCardCreate = {
        cardFrom: this.formCreate.get('numFrom').value,
        cardTo: this.formCreate.get('numTo').value,
        guild: this.formCreate.get('assign').value === '' ?
          undefined : this.formCreate.get('assign').value,
        initialBalance: this.formCreate.get('saldo').value,
      };
      this._guildCardService.guildCardsPost(this.auth.getBearer(), guildCardCreated).subscribe((cards) => {
        this.savingTargetas = false;

        var numTo = this.formCreate.get('numTo').value;
        var numFrom = this.formCreate.get('numFrom').value;
        var numberCard = numTo - numFrom + 1;
		let numberList = '';
		let numberNotList = '';
		var createdCard = '';

        if(cards.length == 0 || cards.length != numberCard){
          for(let o=numFrom;o<=numTo;o++){
			if(cards.find(card => card.cardNumber == o) == undefined){
				numberNotList = numberNotList + o + ", "
			}
			else numberList = numberList + o + ", "
		  }
		  
		  if(numberNotList.length>=1) numberNotList = numberNotList.substring(0,numberNotList.length-2);
		  if(numberList.length>=1) numberList = numberList.substring(0,numberList.length-2);
		
          this.translate.get('TARGETAS.NOT_CREATED', { amount: numberCard-cards.length }).subscribe((message) => {
            this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
          });
          this.translate.get('TARGETAS.NOT_NUMBER_CREATED', { notCreated: numberNotList }).subscribe((message) => {
            this.errorCreatedCard = message;
          });

          
        }
        if(cards.length>0){
          this.translate.get('TARGETAS.CREATED', { amount: numberList }).subscribe((message) => {
            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
          });
        }
  
      }, (err) => {
        this.savingTargetas = false;
        this.toast.error(err);
      });
    }
  }

  /**
  * Carrega els gremis mostrats en el selector de gremis 'assign' de formulari 'formCreate'
  * @returns void
  */
  private loadGremi(): void {
    this._guildService.guildGet(this.auth.getBearer(),null,null,null,null,null,null,true,null).subscribe((gremis) => {
      this.gremis = gremis;
	 
	  //let StringDate = new Date().toLocaleString();
 	   let StringDate = Date.now();  

	  this.gremis = gremis.filter(gremi => Date.parse(gremi.expirationDate) >= StringDate );
    });
  }

 private loadStates(): void {
    this._guildCardService.guildCardStatusGet(this.auth.getBearer(),null,null,true).subscribe((states) => {
      this.states = states;
    });
  }

  private loadMembres(targeta: GuildCard): void {
	if(targeta.guild !== null){
	 this._guildMemberService.guildMemberAllByGuild(targeta.guild.gremiId,this.auth.getBearer(),null,null,null).subscribe((members) => {
		this.members = members;
	});
	}
}

  /**
   * Retorna el filtre actual ({CardFilter}) utilitzat
   * en la cerca i export on es passa como params de query.
   * @returns CardsFilter Interficie que defineix tots els filtres utilitzats en cerca i export
   */
  private getActualSearchFilter(): CardsFilter {
    return <CardsFilter>{
      guildId: this.formSearch.get('nameGremi').value,
      cardNumber: this.formSearch.get('numTargeta').value,
      cardNumberFrom: this.formSearch.get('numTargetaFrom').value,
      cardNumberTo: this.formSearch.get('numTargetaTo').value,
      guildMemberName: this.formSearch.get('nomAgremiat').value,
      guildMemberDni: this.formSearch.get('dniAgremiat').value,
      dateCreatedFrom: this.formSearch.get('dateCreatedFrom').value,
      dateCreatedTo: this.formSearch.get('dateCreatedTo').value,
      dateAssignedGuildFrom: this.formSearch.get('dateAssignGremiFrom').value,
      dateAssignedGuildTo: this.formSearch.get('dateAssignGremiTo').value,
      dateAssignedGuildMemberFrom: this.formSearch.get('dateAssignAgremiatFrom').value,
      dateAssignedGuildMemberTo: this.formSearch.get('dateAssignAgremiatTo').value,
      assignedToGuild: this.formSearch.get('assignedToGremi').value == null ?
        undefined : this.formSearch.get('assignedToGremi').value,
      assignedToGuildMember: this.formSearch.get('assignedToAgremiat').value == null ?
        undefined : this.formSearch.get('assignedToAgremiat').value,
      guildCardStatusId: this.formSearch.get('guildCardStatusId').value
    };
  }


	public modifyTargeta(targeta: GuildCard): void {
	    const dialogRef: MatDialogRef<DialogEditTargetaComponent> = this.dialog.open(
	      DialogEditTargetaComponent,
	      {
	        width: '600px',
	        data: {
	          targeta: targeta,
			  states : this.states
	        }
	      });
		
		dialogRef.afterClosed().subscribe((data) => {
			
			if (data.confirm) {
						this._guildCardService.guildCardUpdate(this.auth.getBearer(),targeta.cardId, data.status).subscribe((message) => {		
						targeta.previousDisabledStatus = targeta.guildCardStatusId;
						targeta.guildCardStatusId = data.status;		
						this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, String("El canvi d'estat s'ha efectuat correctament") );
						}, (err) => {
							this.toast.notify(this.toast.TOAST_TYPES.DANGER, String("El canvi d'estat no s'ha efectuat correctament") );
    					});
			}
			
		});
	}
	
	public estatDesactivada(targeta: GuildCard): boolean {
		return (targeta.guildCardStatusId == 5) 
	}
	
	public massDelete(): void{
	  const dialogRef = this.dialog.open(DialogMassDeleteTargetaComponent, {
      width: '600px',
      data: {
        cards: this.cards
      }
    });

    dialogRef.afterClosed().subscribe((data) => {
      if (data && data.confirm === true) {
		this.translate.get('TARGETAS.MASS_DELETED', {
          }).subscribe((message) => {
		for(let card of this.cards){
			this.updated = true;
      		this._guildCardService.guildCardsIdDelete(card.cardId, this.auth.getBearer()).subscribe((ok) => {
			this.search();
          }, (err) => {
          this.toast.error(err);
		  this.updated = false;
       	  });
		}
		if (this.updated) this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
		else {
			this.translate.get('TARGETAS.MASS_DELETED_ERROR', {
			    	}).subscribe((message) => {
						this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
					});
		}
		
        }, (err) => {
          this.toast.error(err);
        });
      }
	});
	}
	
	public massButtonsAllow(): boolean{
		var DummyCard = this.cards[0];
		for (let card of this.cards){
			if(DummyCard.guild !== null){
				if((card.guild == null || DummyCard.guild.gremiId !== card.guild.gremiId) || card.guildCardStatusId !== DummyCard.guildCardStatusId) {
				    this.allowMassEdition = false;
					return false;
				}
			}
			else if(card.guildCardStatusId !== DummyCard.guildCardStatusId){
				this.allowMassEdition = false;
				return false;
			}
			else{
				if(card.guild !== null){
					this.allowMassEdition = false;
					return false;
				}
			}
		}
		this.allowMassEdition = true;
		return this.allowMassEdition;
	}



	public massEdit(): void{
		const dialogRef = this.dialog.open(DialogMassEditTargetaComponent, {
      width: '600px',
      data: {
        cards: this.cards,
        gremis: this.gremis,
	    auth : this.auth,
		states : this.states
      }
    })

    dialogRef.afterClosed().subscribe((data) => {
    if (data && data.confirm === true) {
		
		if (data.status !== null){
				this.translate.get('TARGETAS.MASS_EDIT', {
				        numTargeta: this.cards.length,
				    	}).subscribe((message) => {	
						for(let card of this.cards){
							this._guildCardService.guildCardUpdate(this.auth.getBearer(),card.cardId, data.status).subscribe((message) => {		
							this.search(); 	
							this.updated = true;				
							},   (err) => {
							this.toast.notify(this.toast.TOAST_TYPES.DANGER, "No s'ha efectuat el canvi en la targeta " + card.cardNumber);
		
					        });
						}
					    if (this.updated) this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
	
						else { 
								this.translate.get('TARGETAS.MASS_EDIT_ERROR', {
						    	}).subscribe((message) => {
									this.toast.notify(this.toast.TOAST_TYPES.DANGER, message);
								});
							}
				 });
				
				
			}
		    else{
				this.updated=true;
				for(let card of this.cards){
					this._guildCardService.guildCardsIdAssignPut(card.cardId, this.auth.getBearer(), data.membre, data.gremi).subscribe((ok) => {
					this.search(); 
					if(this.updated){
						this.updated=false;
						this.translate.get('TARGETAS.MASS_EDIT', {
					        numTargeta: this.cards.length,
				    	}).subscribe((message) => {
						 this.updated = false;
					 	 this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
						},   (err) => {
				          this.toast.error(err);
				        });	
					}
					},   (err) => {
					  this.updated=false;
			          this.toast.error(err);
			        });				
				}
				
			}	
	}
	});
	}
}