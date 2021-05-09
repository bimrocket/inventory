import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app.routing';
import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import {
  MatSelectModule, MatInputModule,
  MatCheckboxModule, MatButtonModule,
  MatRippleModule, MatTooltipModule,
  MatDialogModule, MatProgressSpinnerModule,
  MatIconModule, DateAdapter,
  MatSlideToggleModule
} from '@angular/material';
import { MatTabsModule } from '@angular/material/tabs';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MAT_DATE_LOCALE } from '@angular/material';
import { MatNativeDateModule } from '@angular/material';
import { MatChipsModule } from '@angular/material/chips';
import { ApiModule } from './api';
import { AuthorizationService } from './services/authorization.service';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { TokenInterceptor } from './services/customhttpinterceptor.service';
import { SharedModule } from './shared/shared.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { GestioOperativaComponent } from './gestio-operativa/gestio-operativa.component';
import { ToastService } from './services/toast.service';
import { EditUsersComponent } from './gestio-operativa/edit-users/edit-users.component';
import { LocalizedDatePipe } from './pipes/localizedDate.pipe';
import { DetallDialogComponentComponent } from './gestio-operativa/edit-users/detall-dialog-component/detall-dialog-component.component';
import { UsuarisBoComponent } from './sistema/usuaris-bo/usuaris-bo.component';
import { ParametresComponent } from './sistema/parametres/parametres.component';
import { RolsComponent } from './sistema/rols/rols.component';
import { EditUsuarisBoComponent } from './sistema/edit-usuaris-bo/edit-usuaris-bo.component';
import { DeleteDialogUsuariBoComponent } from './sistema/usuaris-bo/delete-dialog-usuari-bo/delete-dialog-usuari-bo.component';
import { OrderModule } from 'ngx-order-pipe';
import { FilterUsersBoPipe } from './sistema/usuaris-bo/filter-users-bo.pipe';
import { CurrentUserService } from './services/current-user.service';
import { LlistatnotificacionsComponent } from './notificacions/llistat-notificacions.component';
import { EnviamentpushComponent } from './notificacions/enviament-push/enviament-push.component';
import { AddNotificacioComponent } from './notificacions/add-notificacio/add-notificacio.component';
import { BASE_PATH } from './api/variables';
import { MatRadioModule } from '@angular/material/radio';
import { DeleteNotificacioDialogComponent } from './notificacions/delete-notificacio-dialog/delete-notificacio-dialog.component';
import { RefundTicketDialogComponent } from './gestio-operativa/edit-users/refund-ticket-dialog/refund-ticket-dialog.component';
import {
  DialogHistoricTransactionComponent
} from './gestio-operativa/edit-users/dialog-historic-transaction/dialog-historic-transaction.component';
import { CondicionsDUsComponent } from './sistema/condicions-d-us/condicions-d-us.component';
import { PolitiquesPComponent } from './sistema/politiques-p/politiques-p.component';
import { EditCondicionsComponent } from './sistema/edit-condicions/edit-condicions.component';
import { EditPpComponent } from './sistema/edit-pp/edit-pp.component';
import { environment } from './../environments/environment';
import { DialogDeletePPComponent } from './sistema/politiques-p/dialog-delete-pp/dialog-delete-pp.component';
import { ChartsModule } from 'ng2-charts/ng2-charts';
import { DIalogStopTicketComponent } from './gestio-operativa/edit-users/dialog-stop-ticket/dialog-stop-ticket.component';
import { AreesConfiguracionsComponent } from './arees/arees-configuracions/arees-configuracions.component';
import { TramsComponent } from './arees/trams/trams.component';
import { RestricionsComponent } from './arees/restricions/restricions.component';
import { MapService } from './services/map.service';
import { DialogDetallsTramComponent } from './arees/trams/dialog-detalls-tram/dialog-detalls-tram.component';
import { AddTramsComponent } from './arees/trams/add-trams/add-trams.component';
import { VirtualScrollerModule } from 'ngx-virtual-scroller';
import { FooterComponent } from './components/footer/footer.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { AddEditHorariComponent } from './arees/arees-configuracions/add-edit-horari/add-edit-horari.component';
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { DialogDeleteHorariComponent } from './arees/arees-configuracions/dialog-delete-horari/dialog-delete-horari.component';
import { DIalogSelectHolidaysComponent } from './arees/arees-configuracions/dialog-select-holidays/dialog-select-holidays.component';
import { MultipleDatePickerModule } from 'multiple-date-picker-angular';
import { CercadorOperacionsComponent } from './cercador-operacions/cercador-operacions.component';
import { MultiDateCalendarComponent } from './components/multi-date-calendar/multi-date-calendar.component';
import { DialogConfirmComponent } from './gestio-operativa/edit-users/dialog-confirm/dialog-confirm.component';
import { AddEditRestricionsComponent } from './arees/restricions/add-edit-restricions/add-edit-restricions.component';
import { DialogDeleteResComponent } from './arees/restricions/dialog-delete-res/dialog-delete-res.component';
import { EditTramsComponent } from './arees/trams/edit-trams/edit-trams.component';
import { DialogDeleteTramComponent } from './arees/trams/dialog-delete-tram/dialog-delete-tram.component';
import { GestioVigilantsComponent } from './vigilants/gestio-vigilants/gestio-vigilants.component';
import { NotificacionsVigilantsComponent } from './vigilants/notificacions-vigilants/notificacions-vigilants.component';
import { AddEditVigilantsComponent } from './vigilants/gestio-vigilants/add-edit-vigilants/add-edit-vigilants.component';
import { FilterWatcherPipe } from './vigilants/gestio-vigilants/filter-watcher.pipe';
import { DialogDeleteVIgilantsComponent } from './vigilants/gestio-vigilants/dialog-delete-vigilants/dialog-delete-vigilants.component';
import { DialogPosicioTiquetComponent } from './cercador-operacions/dialog-posicio-tiquet/dialog-posicio-tiquet.component';
import { CercadorOperacionsService } from './services/cercador-operacions.service';
import { ExtractesService } from './services/extractes.service';
import { PaginatorService } from './services/paginator.service';
import { PaginatorComponent } from './components/paginator/paginator.component';
import { MatMenuModule } from '@angular/material/menu';
import {
  DialogCreateUserBONoLDAPComponent
} from './sistema/edit-usuaris-bo/dialog-create-user-bono-ldap/dialog-create-user-bono-ldap.component';
import { MyDateAdapter } from './services/date.adapter';
import { GremisComponent } from './gestio-gremi/gremis/gremis.component';
import { AgremiatsComponent } from './gestio-gremi/agremiats/agremiats.component';
import { PermisosComponent } from './gestio-gremi/permisos/permisos.component';
import { VehiclesComponent } from './vehicles/vehicles.component';
import { AddEditGremiComponent } from './gestio-gremi/gremis/add-edit-gremi/add-edit-gremi.component';
import { DeleteGremiComponent } from './gestio-gremi/gremis/delete-gremi/delete-gremi.component';
import { AddEditPermisosComponent } from './gestio-gremi/permisos/add-edit-permisos/add-edit-permisos.component';
import { AddEditVehiclesComponent } from './vehicles/add-edit-vehicles/add-edit-vehicles.component';
import { DeleteVehiclesComponent } from './vehicles/delete-vehicles/delete-vehicles.component';
import { DeletePermisosComponent } from './gestio-gremi/permisos/delete-permisos/delete-permisos.component';
import { DeleteAgremiatsComponent } from './gestio-gremi/agremiats/delete-agremiats/delete-agremiats.component';
import { AddEditAgremiatsComponent } from './gestio-gremi/agremiats/add-edit-agremiats/add-edit-agremiats.component';
import { GestioTargetasComponent } from './gestio-gremi/gestio-targetas/gestio-targetas.component';
import { FakeDataService } from './services/fake-data.service';
import { LocalizedDateOnlyPipe } from './pipes/localizedDate.pipe.1';
import { DialogDeleteCondicionsComponent } from './sistema/condicions-d-us/dialog-delete-condicions/dialog-delete-condicions.component';
import { LoginSSOComponent } from './login/login-sso/login-sso.component';
import { TargetesPipe } from './gestio-gremi/gestio-targetas/targetes.pipe';
import { DialogAssignTargetaComponent } from './gestio-gremi/gestio-targetas/dialog-assign-targeta/dialog-assign-targeta.component';
import { DialogDeleteTargetaComponent } from './gestio-gremi/gestio-targetas/dialog-delete-targeta/dialog-delete-targeta.component';
import { DialogConsultaDGTComponent } from './vehicles/dialog-consulta-dgt/dialog-consulta-dgt.component';
import { DialogEditVehicleComponent } from './vehicles/dialog-edit-vehicle/dialog-edit-vehicle.component';
import { NumericDirective } from './directives/numeric';
import { NavigationEndInterceptor } from './services/navigationEndInterceptor.service';
import { RolesService } from './services/roles.service';
import { DialogEditTargetaComponent } from './gestio-gremi/gestio-targetas/dialog-edit-targeta/dialog-edit-targeta.component';
import { OperacionsTargetaComponent } from './gestio-gremi/operacions-targeta/operacions-targeta.component';
import { GuildCardStatusIDtoTextPipe } from './pipes/guild-card-status-idto-text.pipe';
import { FilterDeletedGuildPipe } from './pipes/filter-deleted-guild.pipe';
import { NotDeletedPipe } from './pipes/not-deleted.pipe';
import { DialogMassEditTargetaComponent } from './gestio-gremi/gestio-targetas/dialog-mass-edit-targeta/dialog-mass-edit-targeta.component';
import { DialogMassDeleteTargetaComponent } from './gestio-gremi/gestio-targetas/dialog-mass-delete-targeta/dialog-mass-delete-targeta.component';
import { ToSixDigitsPipe } from './pipes/to-six-digits.pipe';
import { VehiclesBusComponent } from './vehicles-bus/vehicles-bus.component';
import { DialogConsultaDGTBusComponent } from './vehicles-bus/dialog-consulta-dgt/dialog-consulta-dgt.component';
import { AddEditVehiclesBusComponent } from './vehicles-bus/add-edit-vehicles-bus/add-edit-vehicles-bus.component';
import { DialogEditVehicleBusComponent } from './vehicles-bus/dialog-edit-vehicle-bus/dialog-edit-vehicle-bus.component';

@NgModule({
  imports: [
    BrowserAnimationsModule,
    FormsModule,
    HttpModule,
    RouterModule,
    AppRoutingModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatRippleModule,
    MatInputModule,
    MatTooltipModule,
    MatCheckboxModule,
    ApiModule,
    SharedModule,
    MatTabsModule,
    MatDialogModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    OrderModule,
    MatRadioModule,
    MatProgressSpinnerModule,
    ChartsModule,
    VirtualScrollerModule,
    NgxMaterialTimepickerModule.forRoot(),
    MatIconModule,
    MultipleDatePickerModule,
    MatMenuModule,
    MatChipsModule,
    MatSlideToggleModule
  ],
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    GestioOperativaComponent,
    EditUsersComponent,
    LocalizedDatePipe,
    LocalizedDateOnlyPipe,
    DetallDialogComponentComponent,
    UsuarisBoComponent,
    ParametresComponent,
    RolsComponent,
    EditUsuarisBoComponent,
    DeleteDialogUsuariBoComponent,
    FilterUsersBoPipe,
    FilterWatcherPipe,
    LlistatnotificacionsComponent,
    EnviamentpushComponent,
    AddNotificacioComponent,
    DeleteNotificacioDialogComponent,
    RefundTicketDialogComponent,
    DialogHistoricTransactionComponent,
    CondicionsDUsComponent,
    PolitiquesPComponent,
    EditCondicionsComponent,
    EditPpComponent,
    DialogDeletePPComponent,
    DIalogStopTicketComponent,
    AreesConfiguracionsComponent,
    TramsComponent,
    RestricionsComponent,
    DialogDetallsTramComponent,
    AddTramsComponent,
    FooterComponent,
    NavbarComponent,
    SidebarComponent,
    AddEditHorariComponent,
    DialogDeleteHorariComponent,
    DIalogSelectHolidaysComponent,
    CercadorOperacionsComponent,
    MultiDateCalendarComponent,
    DialogConfirmComponent,
    AddEditRestricionsComponent,
    DialogDeleteResComponent,
    EditTramsComponent,
    DialogDeleteTramComponent,
    GestioVigilantsComponent,
    NotificacionsVigilantsComponent,
    AddEditVigilantsComponent,
    DialogDeleteVIgilantsComponent,
    DialogConfirmComponent,
    DialogPosicioTiquetComponent,
    PaginatorComponent,
    DialogCreateUserBONoLDAPComponent,
    GremisComponent,
    AgremiatsComponent,
    PermisosComponent,
    VehiclesComponent,
    AddEditGremiComponent,
    DeleteGremiComponent,
    AddEditPermisosComponent,
    AddEditVehiclesComponent,
    DeleteVehiclesComponent,
    DeletePermisosComponent,
    DeleteAgremiatsComponent,
    AddEditAgremiatsComponent,
    GestioTargetasComponent,
    DialogDeleteCondicionsComponent,
    LoginSSOComponent,
    TargetesPipe,
    DialogAssignTargetaComponent,
    DialogDeleteTargetaComponent,
    DialogConsultaDGTComponent,
    DialogEditVehicleComponent,
    NumericDirective,
    DialogEditTargetaComponent,
    OperacionsTargetaComponent,
    GuildCardStatusIDtoTextPipe,
    FilterDeletedGuildPipe,
    NotDeletedPipe,
    DialogMassEditTargetaComponent,
    DialogMassDeleteTargetaComponent,
    ToSixDigitsPipe,
    VehiclesBusComponent,
    DialogConsultaDGTBusComponent,
    AddEditVehiclesBusComponent,
    DialogEditVehicleBusComponent
  ],
  entryComponents: [
    DetallDialogComponentComponent,
    DeleteDialogUsuariBoComponent,
    DeleteNotificacioDialogComponent,
    RefundTicketDialogComponent,
    DialogHistoricTransactionComponent,
    DialogDeletePPComponent,
    DIalogStopTicketComponent,
    DialogDetallsTramComponent,
    DialogDeleteHorariComponent,
    DIalogSelectHolidaysComponent,
    DialogConfirmComponent,
    DialogDeleteResComponent,
    DialogDeleteTramComponent,
    DialogDeleteVIgilantsComponent,
    DialogConfirmComponent,
    DialogPosicioTiquetComponent,
    DialogCreateUserBONoLDAPComponent,
    DeleteGremiComponent,
    DeleteVehiclesComponent,
    DeleteAgremiatsComponent,
    DeletePermisosComponent,
    DialogDeleteCondicionsComponent,
    DialogAssignTargetaComponent,
    DialogDeleteTargetaComponent,
    DialogConsultaDGTComponent,
    DialogEditVehicleComponent,
	DialogEditTargetaComponent,
	DialogMassEditTargetaComponent,
	DialogMassDeleteTargetaComponent,
  DialogConsultaDGTBusComponent,
  DialogEditVehicleBusComponent
  ],
  providers: [
    AuthorizationService,
    ToastService,
    CurrentUserService,
    MapService,
    CercadorOperacionsService,
    NavigationEndInterceptor,
    ExtractesService,
    PaginatorService,
    FakeDataService,
    LocalizedDatePipe,
    RolesService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: MAT_DATE_LOCALE,
      useValue: 'ca-ES'
    },
    {
      provide: BASE_PATH,
      useValue: environment.api
      // PRO : 'https://devapi.bsmsa.eu/ext/srvl/bsm/maas/app/v1'
    },
    {
      provide: DateAdapter,
      useClass: MyDateAdapter
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
