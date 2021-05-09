import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { DeleteAgremiatsComponent } from './delete-agremiats/delete-agremiats.component';
import { GuildMemberService } from 'app/api/api/guildMember.service';
import { AuthorizationService } from '../../services/authorization.service';
import { GuildMember } from 'app/api/model/guildMember';
import { AgremiatType, FUNCIONALITIES } from 'app/globalVariables/globalVariables';
import { GuildService } from '../../api';
import { Guild } from '../../api/model/guild';
import { config } from 'config/config';
import { GuildMemberType } from '../../api/model/guildMemberType';
import { CurrentUserService } from '../../services/current-user.service';

export interface AgremiatsFiltre {
  idMember: number,
  cif: string,
  agremiat: string,
  guildId: number,
  email: string
}
@Component({
  selector: 'app-agremiats',
  templateUrl: './agremiats.component.html',
  styleUrls: ['./agremiats.component.scss']
})
export class AgremiatsComponent implements OnInit {

  public formSearch: FormGroup;
  public members: Array<GuildMember>;
  public membersAll: Array<GuildMember>;

  public AgremiatType;
  public guilds: Array<Guild>;
  public types: GuildMemberType[];
  public loadingMembers = false;
  public exportInProgress = false;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    public curUser: CurrentUserService,
    private guildService: GuildService,
    private dialog: MatDialog,
    private toast: ToastService,
    private translate: TranslateService,
    private _guildMember: GuildMemberService,
    private auth: AuthorizationService
  ) {
  }

  public getNomType(idType: number) {
    if (this.types) {
      const type: GuildMemberType = this.types.filter((item) => {
        return item.guildMemberTypeId === idType;
      })[0];
      return type ? type.description : '';
    } else {
      return '';
    }
  }

  ngOnInit() {

    this.AgremiatType = AgremiatType;

    this.guildService.guildGet(this.auth.getBearer()).subscribe((guilds) => {
      this.guilds = guilds;
    });

    this._guildMember.guildMemberType(config.LOCALE, this.auth.getBearer()).subscribe((memberType) => {
      this.types = memberType;
    });

    this.formSearch = new FormGroup({
      idMember: new FormControl(-1),
      cif: new FormControl(undefined),
      agremiat: new FormControl(undefined),
      guild: new FormControl(undefined),
      email: new FormControl(undefined)
    });
    this.search();
  }

  search(): void {
    this.loadingMembers = true;
    const filtre = this.getFiltreActual();
    this._guildMember.guildMember(this.auth.getBearer(),
      filtre.agremiat,
      filtre.email,
      filtre.cif,
      filtre.guildId,
      filtre.idMember,
     ).subscribe((members) => {
        this.members = members;
        this.membersAll = members;
        this.loadingMembers = false;
      },
        (err) => {
          this.loadingMembers = false;
          this.toast.error(err);
        })
  }

  /**
   * Retorna el filtre actual utiltizat per cerca i export
   * @returns AgremiatsFiltre
   */
  private getFiltreActual(): AgremiatsFiltre {
    return {
      agremiat : this.formSearch.get('agremiat').value,
      email : this.formSearch.get('email').value,
      cif : this.formSearch.get('cif').value,
      guildId : this.formSearch.get('guild').value,
      idMember : this.formSearch.get('idMember').value,
    };
  }

  clearInputs() {
    this.formSearch.reset();
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
  public exportAgremiats(): void {
    this.exportInProgress = true;
    const filter: AgremiatsFiltre = this.getFiltreActual();
    this.translate.get('GENERAL.EXPORT_IN_PROGRESS').subscribe((message) => {
      this.toast.notify('warning', message);
      this._guildMember.guildMemberXls(this.auth.getBearer(),
      filter.agremiat,
      filter.email,
      filter.cif,
      filter.guildId,
      filter.idMember,
      config.LOCALE)
        .subscribe((file) => {
          saveAs(file, 'Extracte agremiats del ' + new Date().toLocaleDateString() + '.xls');
          this.exportInProgress = false;
        }, (err) => {
          this.toast.error(err);
          this.exportInProgress = false;
        })

    });


  }


  deleteAgremiat(id: number): void {
    const dialogRef = this.dialog.open(DeleteAgremiatsComponent, {
      width: '600px',
      data: {
        id: id
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this._guildMember.deleteGuildMember(id, this.auth.getBearer()).subscribe(() => {
          this.search();
          this.translate.get('AGREMIATS.DELETED_CORRECTLY_RES').subscribe((message) => {
            this.toast.notify('success', message);
          });
        },
          (err) => { this.toast.error(err); })
      }
    });
  }

}
