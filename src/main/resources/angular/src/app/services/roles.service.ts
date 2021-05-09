import { Injectable } from '@angular/core';
import { PROFILES } from 'app/globalVariables/globalVariables';
import { defaultUrlMatcher } from '@angular/router/src/shared';

@Injectable()
export class RolesService {

    /**
     * Retorna si un rol determinat pot veure o modificar un altre rol
     * @param  {string} currentRol
     * @param  {string} desiredRol
     * @returns boolean
     */
    public isRolAccessible(currentRol: string, desiredRol: string): boolean {
        switch (currentRol) {
            case PROFILES.TOT_ACTIVAT: case PROFILES.ADMIN: case PROFILES.AMB_ADMIN:
                return true;
            case PROFILES.AREA:
                return desiredRol === PROFILES.AREA ||
                    desiredRol === PROFILES.AMB_FINANCE ||
                    desiredRol === PROFILES.AMB_QUERY ||
                    desiredRol === PROFILES.AT_CLIENT_BASIC ||
                    desiredRol === PROFILES.AT_CLIENT_OPERATIU;
            case PROFILES.AT_CLIENT_OPERATIU:
                return desiredRol === PROFILES.AT_CLIENT_BASIC ||
                    desiredRol === PROFILES.AT_CLIENT_OPERATIU;
            case PROFILES.AT_CLIENT_BASIC:
                return desiredRol === PROFILES.AT_CLIENT_BASIC;
            case PROFILES.AMB_FINANCE:
                return desiredRol === PROFILES.AMB_FINANCE;
            case PROFILES.AMB_QUERY:
                return desiredRol === PROFILES.AMB_QUERY;
            default:
                return false;
        }
    }
}