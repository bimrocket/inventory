import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthorizationService } from '../../services/authorization.service';
import { GuildService } from '../../api/api/guild.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { Guild } from 'app/api/model/guild';
import { GuildCard } from '../../api/model/guildCard';
import { GuildCardStatus } from '../../api/model/guildCardStatus';
import { GuildCardCreate } from '../../api/model/guildCardCreate';
import { GuildCardOperation } from '../../api/model/guildCardOperation';
import { GuildCardsService } from '../../api/api/guildCards.service';
import { MatDialog, MatDialogRef } from '@angular/material';
import { GremisFiltre } from '../gremis/gremis.component';
import { config } from 'config/config';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';
import { CurrentUserService } from '../../services/current-user.service';
import {DateAdapter} from '@angular/material';

export interface CardsFilter {
  guildId: number,
  idCard: number,
  numCard: number,
  dateCreated: Date,
  type: string,
}

@Component({
  selector: 'app-operacions-targeta',
  templateUrl: './operacions-targeta.component.html',
  styleUrls: ['./operacions-targeta.component.scss']
})
export class OperacionsTargetaComponent implements OnInit {
	
  public formCreate: FormGroup;
  public formSearch: FormGroup;
  public gremis: Array<Guild>;
  public types: Array<string>;
  public savingTargetas = false;
  public operations: Array<GuildCardOperation>;
  public loadingCards = false;
  public states: Array<GuildCardStatus>;
  public exportInProgress = false;
  public showingResults = false;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    private dialog: MatDialog, public curUser: CurrentUserService,
    private _guildService: GuildService, private translate: TranslateService,
    private toast: ToastService, private auth: AuthorizationService,
    private _guildCardService: GuildCardsService,private dateAdapter:DateAdapter<any>) {
    this.loadGremi();
	this.loadStates();
	this.loadTypes();
  }

  ngOnInit() {
	this.dateAdapter.getFirstDayOfWeek = () => { return 1; }

    this.formSearch = new FormGroup({
      nameGremi: new FormControl(undefined),
      idTargeta: new FormControl(undefined),
	  numTargeta: new FormControl(undefined),
      dateCreated: new FormControl(undefined),
      type: new FormControl(undefined)
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
* Exporta targetas a excel amb filtres actuals
* 1. Bloqueja el botó de exportar
* 2. Notifica al usuari sobre export
* 3. Fa la petició al servidor per fer export
* 4. Notifica si ha hagut error sino descarrega el fitxer
* 5. Desbloqueja el botó de exportació
* @returns void
*/
/*
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
      
     
        config.LOCALE)
        .subscribe((file) => {
          saveAs(file, 'Extracte targetas del ' + new Date().toLocaleDateString() + '.xls');
          this.exportInProgress = false;
        }, (err) => {
          this.toast.error(err);
          this.exportInProgress = false;
        })

    });


  }*/


  /**
  * Carrega els gremis mostrats en el selector de gremis 'assign' de formulari 'formCreate'
  * @returns void
  */
  private loadGremi(): void {
    this._guildService.guildGet(this.auth.getBearer(),null,null,null,null,null,null,null,null).subscribe((gremis) => {
      this.gremis = gremis;
    });
  }

 private loadStates(): void {
    this._guildCardService.guildCardStatusGet(this.auth.getBearer(),null,null,true).subscribe((states) => {
      this.states = states;
    });
  }

  private loadTypes(): void {
	this.types = [];
	this.types.push("Parquímetre","Aparcaments");
}

  /**
   * Retorna el filtre actual ({CardFilter}) utilitzat
   * en la cerca i export on es passa como params de query.
   * @returns CardsFilter Interficie que defineix tots els filtres utilitzats en cerca i export
   */
  private getActualSearchFilter(): CardsFilter {
    return <CardsFilter>{
      guildId: this.formSearch.get('nameGremi').value,
      idCard: this.formSearch.get('idTargeta').value,
      numCard: this.formSearch.get('numTargeta').value,
      dateCreated: this.formSearch.get('dateCreated').value,
      type: this.formSearch.get('type').value,
    };
  }

 
  /**
   * Cerca les targetas amb filtre actual 'CardFilter'
   * obtingut de formulari de cerca 'formSearch'
   * @returns void
   */
  public search(): void {
    this.loadingCards = true;
    const filter: CardsFilter = this.getActualSearchFilter();
    this._guildCardService.operationsList(this.auth.getBearer(),
      filter.guildId,
      filter.idCard,
	  filter.numCard,
      filter.dateCreated,
      filter.type,
    ).subscribe((operations) => {
      this.operations = operations;
      this.loadingCards = false;
      this.showingResults = true;
    }, (err) => {
      this.toast.error(err);
      this.loadingCards = false;
    });
  }

	public exportOperacions(): void {
	    this.exportInProgress = true;
	    const filter: CardsFilter = this.getActualSearchFilter();
	    this.translate.get('GENERAL.EXPORT_IN_PROGRESS').subscribe((message) => {
	   	    this.toast.notify('warning', message);
		    this._guildCardService.guildCardsOperationsXlsGet(this.auth.getBearer(),
		      filter.guildId,
		      filter.idCard,
			  filter.numCard,
		      filter.dateCreated,
		      filter.type,
			  config.LOCALE)
		    .subscribe((file) => {
			      saveAs(file, 'Extracte operacions del ' + new Date().toLocaleDateString() + '.xls');
	          this.exportInProgress = false;
	        }, (err) => {
	          this.toast.error(err);
	          this.exportInProgress = false;
	        })
	    });
	}

   public getGuildNameById(id: Number) : string {
		return this.gremis.find(x => x.gremiId === id).name;
	}
	
	public getImportOperacio(iniBalance: number, currBalance: number) : string {
		return (iniBalance - currBalance).toFixed(2); 
	}
	

	

}


