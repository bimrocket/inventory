import { Component, OnInit } from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { GuildService } from '../../api/api/guild.service';
import { NotificationService, JuridicEntity } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';

import { MatDialog } from '@angular/material';
import { DeleteGremiComponent } from './delete-gremi/delete-gremi.component';
import { PaginatorService } from '../../services/paginator.service';
import { DataService } from '../../api/api/data.service';
import { Guild } from 'app/api/model/guild';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';
import { CurrentUserService } from '../../services/current-user.service';
import {DateAdapter} from '@angular/material';

export interface GremisFiltre {
  formData: GremisFormData,
  dum: boolean,
  areaVerdaPlusAparcaments: boolean
}

export interface GremisFormData {
  dni: string,
  permis: string[],
  email: string,
  idGremi: number,
  nameContact: string,
  telContact: number,
  cadConveni: Date
}

@Component({
  selector: 'app-gremis',
  templateUrl: './gremis.component.html',
  styleUrls: ['./gremis.component.scss']
})
export class GremisComponent implements OnInit {

  public searchForm: FormGroup;
  public gremis: Array<Guild>;
  public gremisAll: Array<Guild>;
  public municipis;
  public loadingGremis = false;
  public exportInProgress = false;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    private data: DataService, public curUser: CurrentUserService,
    private auth: AuthorizationService, private _dialog: MatDialog, private _notificationsService: NotificationService,
    private translate: TranslateService, private _guildService: GuildService,private dateAdapter: DateAdapter<any>,
    private toast: ToastService, public paginator: PaginatorService) {
  }

  /**
   * Inicialització del component
   * @returns void
   */
  public ngOnInit(): void {
    this.dateAdapter.getFirstDayOfWeek = () => { return 1; }
    // Init de form
    this.searchForm = new FormGroup({
      dni: new FormControl(undefined),
      permis: new FormControl(undefined),
      email: new FormControl(undefined),
      idGremi: new FormControl(undefined),
      nameContact: new FormControl(undefined),
      telContact: new FormControl(undefined),
      cadConveni: new FormControl(undefined)
    });

    // Recuperem ciutats necessaris per mostrar en taula de guilds
    this.data.getCities(true).subscribe((cities) => {
      this.municipis = cities;
      // Cercem tots els guilds
      this.search();
    }, (err) => {
      this.toast.error(err);
      this.loadingGremis = false;
    });

  }

  /**
   * Cerca gremis amb filtres actuals
   * @returns void
   */
  public search(val:String = null): void {
    this.loadingGremis = true;
    const gremiFilter: GremisFiltre = this.getFormDataForFilter();

    this._guildService.guildGet(this.auth.getBearer(),
      gremiFilter.formData.dni,
      gremiFilter.formData.cadConveni ? gremiFilter.formData.cadConveni.toISOString() : undefined,
      gremiFilter.formData.email,
      gremiFilter.formData.nameContact,
      gremiFilter.formData.idGremi,
      gremiFilter.dum,
      gremiFilter.areaVerdaPlusAparcaments,
      gremiFilter.formData.telContact).subscribe((gremis) => {
        this.gremis = gremis;
        if(val == null){
          this.gremisAll = gremis;
        }
        this.loadingGremis = false;
      }, (err) => {
        this.loadingGremis = false;
        this.toast.error(err);
      });
  }

  /**
   * Exporta gremis a excel amb filtres actuals
   * 1. Bloqueja el botó de exportar
   * 2. Notifica al usuari sobre export
   * 3. Fa la petició al servidor per fer export
   * 4. Notifica si ha hagut error sino descarrega el fitxer
   * 5. Desbloqueja el botó de exportació
   * @returns void
   */
  public exportGremis(): void {
    this.exportInProgress = true;
    const gremiFilter: GremisFiltre = this.getFormDataForFilter();
    this.translate.get('GENERAL.EXPORT_IN_PROGRESS').subscribe((message) => {
      this.toast.notify('warning', message);
      this._guildService.guildXlsGet(this.auth.getBearer(),
        gremiFilter.formData.dni,
        gremiFilter.formData.cadConveni ? gremiFilter.formData.cadConveni.toISOString() : undefined,
        gremiFilter.formData.email,
        gremiFilter.formData.nameContact,
        gremiFilter.formData.idGremi,
        gremiFilter.dum,
        gremiFilter.areaVerdaPlusAparcaments,
        gremiFilter.formData.telContact)
        .subscribe((file) => {
          saveAs(file, 'Extracte gremis del ' + new Date().toLocaleDateString() + '.xls');
          this.exportInProgress = false;
        }, (err) => {
          this.toast.error(err);
          this.exportInProgress = false;
        })

    });


  }

  /**
   * Returns GremisFiltre used in petition to search and export
   * Contains formData {GremisFormData} values and if dum and areaVerda are checked
   * @returns {GremisFiltre}
   */
  private getFormDataForFilter(): GremisFiltre {
    const valuesSearch = this.searchForm.value;

    let areaVerdaPlusAparcaments = undefined;
    let dum = undefined;
    if (valuesSearch.permis === null ||
      valuesSearch.permis === undefined ||
      valuesSearch.permis === '' ||
      valuesSearch.permis.length === 0) {
      valuesSearch.permis = '';
    }

    if (valuesSearch.permis !== '') {
      valuesSearch.permis.forEach(element => {
        if (element === 'dum') {
          dum = true;
        }
        if (element === 'areaVerdaPlusAparcaments') {
          areaVerdaPlusAparcaments = true;
        }
      });
    }
    return {
      formData: valuesSearch,
      areaVerdaPlusAparcaments: areaVerdaPlusAparcaments,
      dum: dum
    };
  }

  /**
   * Netejar filtres
   * @returns void
   */
  public clearInputs(): void {
    this.searchForm.reset();
    this.search();
  }
  /**
   * Mostra dialog de eliminar vehicle un cop Confirmat fa la
   * petició de eliminar el vehicle i notifica amb toast el usuari
   * sobre el estat de petició (si ha sigut correcte o ha fallat)
   * @param  {number} id
   * @returns void
   */
  public deleteGremi(id: number): void {
    const dialogRef = this._dialog.open(DeleteGremiComponent, {
      width: '600px',
      data: {
        id: id
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this._guildService.guildGuildIdDelete(id, this.auth.getBearer()).subscribe(() => {
          this.translate.get('GREMIS.DELETED_CORRECTLY_RES').subscribe((message) => {
            this.toast.notify('success', message);
            this.search();
          }, (err) => {
            this.toast.error(err);
          });
        });
      }
    });
  }



}
