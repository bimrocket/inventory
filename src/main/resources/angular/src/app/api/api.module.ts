import { NgModule, ModuleWithProviders, SkipSelf, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { Configuration } from './configuration';

import { AuditService } from './api/audit.service';
import { ConfigurationService } from './api/configuration.service';
import { NotificationService } from './api/notification.service';
import { ParkService } from './api/park.service';
import { ParkingService } from './api/parking.service';
import { PaymentGatewayService } from './api/paymentGateway.service';
//import { ThirdUsersService } from './api/thirdUsers.service';
import { TicketsService } from './api/tickets.service';
import { UserService } from './api/user.service';
import { UserboService } from './api/userbo.service';
import { VehicleBusService } from './api/vehicleBus.service';
import { VehicleService } from './api/vehicle.service';
import { DataService } from './api/data.service';
import { TermsService } from './api/terms.service';
import { CityConfigService } from './api/cityConfig.service';
import { WatcherService } from './api/watcher.service';
import { OauthService } from './api/oauth.service';
import { ServiceService } from './api/service.service';
import { GuildService } from './api/guild.service';
import { GuildMemberService } from './api/guildMember.service';
import { GuildCardsService } from './api/guildCards.service';
import { SegmentsService } from './api/segments.service';
import { PermissionService } from './api/permission.service';
import { VehiclesService } from './api/vehicles.service';

@NgModule({
  imports:      [ CommonModule, HttpClientModule, HttpModule ],
  declarations: [],
  exports:      [],
  providers: [
    AuditService,
    ConfigurationService,
    NotificationService,
    ParkService,
    ParkingService,
    PaymentGatewayService,
    //ThirdUsersService,
    TicketsService,
    UserService,
    UserboService,
    VehicleService,
    VehicleBusService,
    DataService,
    TermsService,
    CityConfigService,
    WatcherService,
    OauthService,
    ServiceService,
    GuildService,
    GuildMemberService,
    GuildCardsService,
    SegmentsService,
    PermissionService,
    VehiclesService
 ]
})
export class ApiModule {
    public static forRoot(configurationFactory: () => Configuration): ModuleWithProviders {
        return {
            ngModule: ApiModule,
            providers: [ { provide: Configuration, useFactory: configurationFactory } ]
        }
    }

    constructor( @Optional() @SkipSelf() parentModule: ApiModule) {
        if (parentModule) {
            throw new Error('ApiModule is already loaded. Import your base AppModule only.');
        }
    }
}
