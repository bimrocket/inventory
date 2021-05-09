import { NgModule } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';

import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GestioOperativaComponent } from './gestio-operativa/gestio-operativa.component';
import { EditUsersComponent } from './gestio-operativa/edit-users/edit-users.component';
import { UsuarisBoComponent } from './sistema/usuaris-bo/usuaris-bo.component';
import { RolsComponent } from './sistema/rols/rols.component';
import { ParametresComponent } from './sistema/parametres/parametres.component';
import { EditUsuarisBoComponent } from './sistema/edit-usuaris-bo/edit-usuaris-bo.component';
import { LlistatnotificacionsComponent } from './notificacions/llistat-notificacions.component';
import { EnviamentpushComponent } from './notificacions/enviament-push/enviament-push.component';
import { AddNotificacioComponent } from './notificacions/add-notificacio/add-notificacio.component';
import { CondicionsDUsComponent } from './sistema/condicions-d-us/condicions-d-us.component';
import { PolitiquesPComponent } from './sistema/politiques-p/politiques-p.component';
import { EditCondicionsComponent } from './sistema/edit-condicions/edit-condicions.component';
import { EditPpComponent } from './sistema/edit-pp/edit-pp.component';
import { AreesConfiguracionsComponent } from './arees/arees-configuracions/arees-configuracions.component';
import { TramsComponent } from './arees/trams/trams.component';
import { RestricionsComponent } from './arees/restricions/restricions.component';
import { AddTramsComponent } from './arees/trams/add-trams/add-trams.component';
import { AddEditHorariComponent } from './arees/arees-configuracions/add-edit-horari/add-edit-horari.component';
import { CercadorOperacionsComponent } from './cercador-operacions/cercador-operacions.component';
import { AddEditRestricionsComponent } from './arees/restricions/add-edit-restricions/add-edit-restricions.component';
import { EditTramsComponent } from './arees/trams/edit-trams/edit-trams.component';
import { GremisComponent } from './gestio-gremi/gremis/gremis.component';
import { AgremiatsComponent } from './gestio-gremi/agremiats/agremiats.component';
import { PermisosComponent } from './gestio-gremi/permisos/permisos.component';
import { AddEditPermisosComponent } from './gestio-gremi/permisos/add-edit-permisos/add-edit-permisos.component';
import { VehiclesComponent } from './vehicles/vehicles.component';
import { AddEditVehiclesComponent } from './vehicles/add-edit-vehicles/add-edit-vehicles.component';
import { AddEditGremiComponent } from './gestio-gremi/gremis/add-edit-gremi/add-edit-gremi.component';
import { AddEditAgremiatsComponent } from './gestio-gremi/agremiats/add-edit-agremiats/add-edit-agremiats.component';
import { GestioTargetasComponent } from './gestio-gremi/gestio-targetas/gestio-targetas.component';
import { OperacionsTargetaComponent } from './gestio-gremi/operacions-targeta/operacions-targeta.component';
import { LoginSSOComponent } from './login/login-sso/login-sso.component';
import { FUNCIONALITIES } from './globalVariables/globalVariables';
import { VehiclesBusComponent } from './vehicles-bus/vehicles-bus.component';
import { AddEditVehiclesBusComponent } from './vehicles-bus/add-edit-vehicles-bus/add-edit-vehicles-bus.component';
export interface DUMRoutes {
  path: string,
  component?: any,
  redirectTo?: string,
  pathMatch?: string,
  permissions?: any
}

export const DUM_ROUTES: DUMRoutes[] = [
  {
    path: '',
    redirectTo: 'inici',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'login/sso',
    component: LoginSSOComponent
  },
  {
    path: 'inici',
    component: DashboardComponent
  },
  {
    path: 'gestio-operativa',
    component: GestioOperativaComponent,
    permissions: FUNCIONALITIES.USER_QUERY
  },
  {
    path: 'gestio-operativa/:id',
    component: EditUsersComponent,
    permissions: FUNCIONALITIES.USER_QUERY
  },
  {
    path: 'cercador-operacions',
    component: CercadorOperacionsComponent,
    permissions: FUNCIONALITIES.OPERATIONS_QUERY
  },
  {
    path: 'sistema/usuaris-bo',
    component: UsuarisBoComponent,
    permissions: FUNCIONALITIES.USERBO_QUERY
  },
  {
    path: 'sistema/usuaris-bo/:id',
    component: EditUsuarisBoComponent,
    permissions: FUNCIONALITIES.USERBO_MANAGEMENT
  },
  {
    path: 'sistema/usuaris-bo/new',
    component: EditUsuarisBoComponent,
    permissions: FUNCIONALITIES.USERBO_MANAGEMENT
  },
  {
    path: 'sistema/rols',
    component: RolsComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/parametres',
    component: ParametresComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'notificacions/llista',
    component: LlistatnotificacionsComponent,
    permissions: FUNCIONALITIES.NOTIFICATIONS_MANAGEMENT
  },
  {
    path: 'notificacions/push',
    component: EnviamentpushComponent,
    permissions: FUNCIONALITIES.NOTIFICATIONS_MANAGEMENT
  },
  {
    path: 'notificacions/:id',
    component: AddNotificacioComponent,
    permissions: FUNCIONALITIES.NOTIFICATIONS_MANAGEMENT
  },
  {
    path: 'notificacions/new',
    component: AddNotificacioComponent,
    permissions: FUNCIONALITIES.NOTIFICATIONS_MANAGEMENT
  },
  {
    path: 'sistema/condicions',
    component: CondicionsDUsComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/condicions/new',
    component: EditCondicionsComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/condicions/:id',
    component: EditCondicionsComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/pp',
    component: PolitiquesPComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/pp/new',
    component: EditPpComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'sistema/pp/:id',
    component: EditPpComponent,
    permissions: FUNCIONALITIES.SYSTEM_ADMIN
  },
  {
    path: 'arees/configuracions',
    component: AreesConfiguracionsComponent,
    permissions: FUNCIONALITIES.AREA_QUERY
  },
  {
    path: 'arees/horaris/new',
    component: AddEditHorariComponent,
    permissions: FUNCIONALITIES.AREA_QUERY
  },
  {
    path: 'arees/horaris/:id',
    component: AddEditHorariComponent,
    permissions: FUNCIONALITIES.AREA_QUERY
  },
  {
    path: 'arees/trams',
    component: TramsComponent,
    permissions: FUNCIONALITIES.AREA_QUERY
  },
  {
    path: 'arees/trams/new',
    component: AddTramsComponent,
    permissions: FUNCIONALITIES.AREA_MANAGEMENT
  },
  {
    path: 'arees/trams/:id',
    component: EditTramsComponent,
    permissions: FUNCIONALITIES.AREA_MANAGEMENT
  },
  {
    path: 'arees/restricions',
    component: RestricionsComponent,
    permissions: FUNCIONALITIES.AREA_QUERY
  },
  {
    path: 'arees/restricions/new',
    component: AddEditRestricionsComponent,
    permissions: FUNCIONALITIES.AREA_MANAGEMENT
  },
  {
    path: 'arees/restricions/:id',
    component: AddEditRestricionsComponent,
    permissions: FUNCIONALITIES.AREA_MANAGEMENT
  },
  /*{
    path: 'vigilants/gestio',
    component: GestioVigilantsComponent,
    permissions: FUNCIONALITIES.WATCHER_QUERY
  },
  {
    path: 'vigilants/notificacions',
    component: NotificacionsVigilantsComponent,
    permissions: FUNCIONALITIES.WATCHER_MANAGEMENT
  },
  {
    path: 'vigilants/gestio/new',
    component: AddEditVigilantsComponent,
    permissions: FUNCIONALITIES.WATCHER_MANAGEMENT
  },
  {
    path: 'vigilants/gestio/:id',
    component: AddEditVigilantsComponent,
    permissions: FUNCIONALITIES.WATCHER_MANAGEMENT
  },*/
  {
    path: 'gestio-gremi/gremis',
    component: GremisComponent,
    permissions: FUNCIONALITIES.GUILD_QUERY
  },
  {
    path: 'gestio-gremi/gremis/:id',
    component: AddEditGremiComponent,
    permissions: FUNCIONALITIES.GUILD_MANAGEMENT
  },
  {
    path: 'gestio-gremi/agremiats',
    component: AgremiatsComponent,
    permissions: FUNCIONALITIES.GUILD_QUERY
  },
  {
    path: 'gestio-gremi/agremiats/:id',
    component: AddEditAgremiatsComponent,
    permissions: FUNCIONALITIES.GUILD_MANAGEMENT
  },
  {
    path: 'gestio-gremi/permisos',
    component: PermisosComponent,
    permissions: FUNCIONALITIES.GUILD_QUERY
  },
  {
    path: 'gestio-gremi/permisos/:id',
    component: AddEditPermisosComponent,
    permissions: FUNCIONALITIES.GUILD_MANAGEMENT
  },
  {
    path: 'vehicles',
    component: VehiclesComponent,
    permissions: FUNCIONALITIES.VEHICLES_QUERY
  },
  {
    path: 'vehicles/:id',
    component: AddEditVehiclesComponent,
    permissions: FUNCIONALITIES.VEHICLES_MANAGEMENT
  },
  {
    path: 'vehicles-bus',
    component: VehiclesBusComponent,
    permissions: FUNCIONALITIES.VEHICLES_QUERY
  },
  {
    path: 'vehicles-bus/:id',
    component: AddEditVehiclesBusComponent,
    permissions: FUNCIONALITIES.VEHICLES_QUERY  
  },
  {
    path: 'gestio-gremi/gestio-targetas',
    component: GestioTargetasComponent,
    permissions: FUNCIONALITIES.GUILD_QUERY
  },
  {
	path: 'gestio-gremi/operacions-targeta',
	component: OperacionsTargetaComponent,
	permissions: FUNCIONALITIES.GUILD_QUERY
  }
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(DUM_ROUTES)
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
