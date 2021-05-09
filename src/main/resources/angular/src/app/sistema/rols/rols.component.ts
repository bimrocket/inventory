import { Component, OnInit } from '@angular/core';
import { UserboService } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { ToastService } from '../../services/toast.service';
import { Observable } from 'rxjs';
import { Profile } from '../../api/model/profile';
import { Funcionality } from '../../api/model/funcionality';
import { TranslateService } from '@ngx-translate/core';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';

@Component({
  selector: 'app-rols',
  templateUrl: './rols.component.html',
  styleUrls: ['./rols.component.scss']
})
export class RolsComponent implements OnInit {

  public profiles: Profile[];
  public funcionalities: Funcionality[] = [];
  public loadingRols = true;

  constructor(
    private toast: ToastService, private translate: TranslateService,
    private userBoService: UserboService, private auth: AuthorizationService) {
  }

  /**
   * Inicialitza el component
   * 1. Carrega els perfils
   * 2. Carrega les funcionalitats
   * @returns void
   */
  public ngOnInit(): void {
    this.userBoService.getAllProfiles().subscribe((profiles: Profile[]) => {
      this.profiles = profiles;
      this.userBoService.getAllFuncionalities().subscribe((funcionalities: Funcionality[]) => {
        this.funcionalities = funcionalities;
        this.loadingRols = false;
      }, (err) => {
        this.toast.error(err);
        this.loadingRols = false;
      });
    }, (err) => {
      this.toast.error(err);
      this.loadingRols = false;
    });
  }

  /**
   * Comprova si una funcionalitat esta habilitada per un perfil determinat
   * @param  {Profile} profile Profile a comprovar
   * @param  {Funcionality} func Funcionalitat a comprovar
   * @returns boolean true or false
   */
  public isSelected(profile: Profile, func: Funcionality): boolean {
    let found = false;
    profile.funcionalities.forEach(func2 => {
      if (func2.key === func.key) {
        found = true;
      }
    });
    return found;
  }

  /**
   * Habilita/deshabilita funcionalitat per un perfil determinat
   * Si funcionalitat es gestió també comprova si consulta està 
   * habilitada i en cas contrari la habilita
   * @param  {Profile} profile Perfil a modificar
   * @param  {Funcionality} func Funcionalitat a habilita/deshabilitar
   * @returns void
   */
  public toggleFunc(profile: Profile, func: Funcionality): void {
    if (this.isSelected(profile, func)) {
      let indexFound = -1;
      for (let index = 0; index < profile.funcionalities.length; index++) {
        if (profile.funcionalities[index].funcionalityId === func.funcionalityId) {
          indexFound = index;
        }
      }
      profile.funcionalities.splice(indexFound, 1);
    } else {
      profile.funcionalities.push(func);
      // Si es gestió habilitem sempre la consulta
      switch (func.key) {
        case FUNCIONALITIES.USER_MANAGEMENT:
          const userQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.USER_QUERY);
          if (!this.isSelected(profile, userQuery)) {
            this.toggleFunc(profile, userQuery);
          }
          break;
        case FUNCIONALITIES.USERBO_MANAGEMENT:
          const userBoQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.USERBO_QUERY);
          if (!this.isSelected(profile, userBoQuery)) {
            this.toggleFunc(profile, userBoQuery);
          }
          break;
        case FUNCIONALITIES.AREA_MANAGEMENT:
          const areaQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.AREA_QUERY);
          if (!this.isSelected(profile, areaQuery)) {
            this.toggleFunc(profile, areaQuery);
          }
          break;
        case FUNCIONALITIES.GUILD_MANAGEMENT:
          const guildQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.GUILD_QUERY);
          if (!this.isSelected(profile, guildQuery)) {
            this.toggleFunc(profile, guildQuery);
          }
          break;
        case FUNCIONALITIES.OPERATIONS_MANAGEMENT:
          const operationsQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.OPERATIONS_QUERY);
          if (!this.isSelected(profile, operationsQuery)) {
            this.toggleFunc(profile, operationsQuery);
          }
          break;
        case FUNCIONALITIES.VEHICLES_MANAGEMENT:
          const vehicleQuery: Funcionality = this.getFuncionalityByKey(FUNCIONALITIES.VEHICLES_QUERY);
          if (!this.isSelected(profile, vehicleQuery)) {
            this.toggleFunc(profile, vehicleQuery);
          }
          break;
        default:
          // Do Nothing
          break;
      }
    }
  }
  
  /**
   * Guarda els perfils i els seus permisos definits
   * 1. Concatena totes les peticions de guardar perfil en una array
   * 2. Executa totes les peticions en parallel
   * 3. Si tots s'han executat correctament notifica al usuari que els perfils s'han actualtizat correctament.
   * 4. En cas contarari mostra error del servidor.
   * @returns void
   */
  public saveRols(): void {
    const joinRequests: Promise<Profile>[] = [];
    this.profiles.forEach(profile => {
      joinRequests.push(this.userBoService.updateProfile(profile.profileId, this.auth.getBearer(), profile.funcionalities)
        .toPromise());
    });
    Promise.all(joinRequests).then((ok) => {
      this.translate.get('ROLS.UPDATED_OK').subscribe((message) => {
        this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
      });
    }, (err) => {
      this.toast.error(err);
    }).catch((err) => {
      this.toast.error(err);
    });
  }

  /**
   * Comprova si un funcionalitat d'un perfil no es pot modificar
   * - En cas de que estigui enabled la funcionlitat de edició 
   *    la consulta sempre estarà habilitada i no es pot modificar
   * - En cas de que sigui profile.profileId === 1 (Perfil Tot activat)
   * @param  {Profile} profile Perfil a comprovar
   * @param  {Funcionality} funcionality Funcionalitat a comprovar
   * @returns boolean true or false
   */
  public isDisabledFuncionality(profile: Profile, funcionality: Funcionality): boolean {
    if (profile.profileId === 1) {
      return true;
    } else {
      switch (funcionality.key) {
        case FUNCIONALITIES.USER_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.USER_MANAGEMENT));
        case FUNCIONALITIES.USERBO_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.USERBO_MANAGEMENT));
        case FUNCIONALITIES.AREA_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.AREA_MANAGEMENT));
        case FUNCIONALITIES.GUILD_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.GUILD_MANAGEMENT));
        case FUNCIONALITIES.OPERATIONS_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.OPERATIONS_MANAGEMENT));
        case FUNCIONALITIES.VEHICLES_QUERY:
          return this.isSelected(profile, this.getFuncionalityByKey(FUNCIONALITIES.VEHICLES_MANAGEMENT));
        default:
          return false;
      }
    }
  }

  /**
   * Retorna una funcionalitat a partir del seu key identificatiu
   * @param  {string} key Key identificatiu
   * @returns Funcionality Funcionlitat encontrada
   */
  private getFuncionalityByKey(key: string): Funcionality {
    return this.funcionalities.find((func) => {
      return func.key === key;
    })
  }

}
