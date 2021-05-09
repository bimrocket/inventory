import { Injectable, EventEmitter } from '@angular/core';
import { UserboService, JuridicEntity } from '../api';

import * as jwt_decode from 'jwt-decode';
import { AuthorizationService } from './authorization.service';
import { Router, NavigationEnd } from '@angular/router';
import { config } from 'config/config';
import { DataService } from 'app/api/api/data.service';
import { timeout } from 'rxjs/operators';
import { APLICATION } from '../globalVariables/globalVariables';

export interface AccessTokenJWT {
  access_token: string,
  refresh_token: string,
  token_type: string,
  expires_in: number,
  client_id: string,
  scope: string,
  jti: string,
  profile: string,
  fullName: string,
  jurEntities?: number[],
  AREA_MANAGEMENT?: boolean,
  AREA_QUERY?: boolean,
  GUILD_MANAGEMENT?: boolean,
  GUILD_QUERY?: boolean,
  NOTIFICATIONS_MANAGEMENT?: boolean,
  OPERATIONS_MANAGEMENT?: boolean,
  OPERATIONS_QUERY?: boolean,
  SYSTEM_ADMIN?: boolean,
  USERBO_MANAGEMENT?: boolean,
  USERBO_QUERY?: boolean,
  USER_MANAGEMENT?: boolean,
  USER_QUERY?: boolean,
  WATCHER_MANAGEMENT?: boolean,
  WATCHER_QUERY?: boolean
};

@Injectable()
export class CurrentUserService {
  private info: AccessTokenJWT;
  private municipisEventEmitter: EventEmitter<JuridicEntity[]> = new EventEmitter<JuridicEntity[]>();
  public canAllMunicipis: boolean;

  constructor(
    private _auth: AuthorizationService, private router: Router) {
      this.canAllMunicipis = true;
      this.loadUser();
  }

  /**
   * Carrega el JSON Web Token , muncipi seleccionat i municipis accessibles
   * @returns void
   */
  public loadUser(): void {
    this.info = this.getDecodedAccessToken(this._auth.getAccessToken());
    if (this.info == null && this.router.url !== '/login' && this.router.url !== '/') {
      this.router.navigate(['/login']);
    } else if (this.info != null) {
      if (!this.info.jurEntities || this.info.jurEntities.length === 0) {
        if (!this.getMunicipi()) {
          this.setMunicipi(config.MUNICIPI_BARCELONA_ID);
        }
        this.canAllMunicipis = true;
      } else {
        if (!this.getMunicipi()) {
          this.setMunicipi(this.info.jurEntities[0]);
        }
        this.canAllMunicipis = false;
      }
      if(this.isSPRO()){
        this.setProject(APLICATION.ZONA_DUM);
      }
    }
  }

  /**
   * Retorna si el usuari actual té permis de sistemas
   * @returns boolean true or false
   */
  public isAdmin(): boolean {
    if (this.info) {
      return this.info.SYSTEM_ADMIN;
    } else {
      return false;
    }
  }

  /**
   * Retorna si el usuari actual té un permís
   * @param  {any} perm Key o array amb keys del permisos
   * @returns boolean True si conté el permís , otherwise false
   */
  public hasPermission(perm: any): boolean {
    if (this.info) {
      if (typeof(perm) === 'string') {
        return this.info[perm];
      } else if (typeof(perm) === 'object' && perm.length > 0) {
        let found = false;
        for (let index = 0; index < perm.length && !found; index++) {
          const element = perm[index];
          found = this.info[element]
        }

        return found;
      }
    } else {
      return false;
    }
  }

  /**
   * Retorna el municipi del usuari
   * @returns number Num del municipi actual
   */
  public getMunicipi(): number {
    return parseFloat(localStorage.getItem(config.LOCAL_STORAGE_KEYS.DUM_MUNICIPI));
  }

  /**
   * Guarda el municipi del usuari
   * @param  {number} municipi
   * @returns void
   */
  public setMunicipi(municipi: number): void {
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.DUM_MUNICIPI, municipi.toString());
  }

  /**
   * Guarda el projecte que esta veient
   * @param  {number} project
   * @returns void
   */
  public setProject(project: number): void {
    localStorage.setItem(config.LOCAL_STORAGE_KEYS.PROJECT, project.toString());
  }

  public isSPRO(): Boolean {
    return (parseFloat(localStorage.getItem(config.LOCAL_STORAGE_KEYS.PROJECT)) == APLICATION.ZONA_DUM) || localStorage.getItem(config.LOCAL_STORAGE_KEYS.PROJECT) == null;
  }

  public isZonaBus(): Boolean {
    return (parseFloat(localStorage.getItem(config.LOCAL_STORAGE_KEYS.PROJECT)) == APLICATION.ZONA_BUS);
  }

    /**
   * Retorna el project del usuari
   * @returns number Num del project actual
   */
  public getProject(): number {
    return parseFloat(localStorage.getItem(config.LOCAL_STORAGE_KEYS.PROJECT));
  }

  /**
   * Retorna els municipis accessibles al usuari actual
   * @returns EventEmitter<JuridicEntity[]> emitter dels events on es pot subscriure i obtenir municipis un cop carregats
   */
  public getAccessibleMunicipisEvent(): EventEmitter<JuridicEntity[]> {
    return this.municipisEventEmitter;
  }

  /**
   * Retorna el nom complert del usuari actual
   * @returns string
   */
  public getFullName(): string {
    if (this.info) {
      return this.info.fullName;
    } else {
      return '';
    }
  }

  /**
   * Retorna el perfil del usuari actual
   * @returns string
   */
  public getProfile(): string {
    if (this.info) {
      return this.info.profile;
    } else {
      return '';
    }
  }


  public getUserMunicipis(): number[] {
    if (this.info) {
      return this.info.jurEntities;
    } else {
      return [];
    }
  }

  /**
   * Descodifica el access token al objecte AccessTokenJWT
   * @param  {string} token Token a descodificar
   * @returns AccessTokenJWT Token com objecte typescript
   */
  private getDecodedAccessToken(token: string): AccessTokenJWT {
    try {
      return jwt_decode(token);
    } catch (Error) {
      return null;
    }
  }
}
