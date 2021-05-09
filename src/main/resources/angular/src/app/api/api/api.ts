export * from './audit.service';
import { AuditService } from './audit.service';
export * from './configuration.service';
import { ConfigurationService } from './configuration.service';
export * from './notification.service';
import { NotificationService } from './notification.service';
export * from './park.service';
import { ParkService } from './park.service';
export * from './parking.service';
import { ParkingService } from './parking.service';
export * from './paymentGateway.service';
import { PaymentGatewayService } from './paymentGateway.service';
/*export * from './thirdUsers.service';
import { ThirdUsersService } from './thirdUsers.service';*/
export * from './tickets.service';
import { TicketsService } from './tickets.service';
export * from './user.service';
import { UserService } from './user.service';
export * from './userbo.service';
import { UserboService } from './userbo.service';
export * from './vehicle.service';
import { VehicleService } from './vehicle.service';
export * from './guildCards.service';
import { GuildCardsService } from './guildCards.service';
export * from './guild.service';
import { GuildService } from './guild.service';
export * from './guildMember.service';
import { GuildMemberService } from './guildMember.service';
export * from './permission.service';
import { PermissionService } from './permission.service';
export * from './vehicleBus.service'
import { VehicleBusService } from './vehicleBus.service';

export const APIS = [AuditService, ConfigurationService, NotificationService, ParkService, ParkingService, PaymentGatewayService, TicketsService, UserService, UserboService, VehicleService,GuildMemberService,GuildService,GuildCardsService,PermissionService,VehicleBusService];
//export const APIS = [AuditService, ConfigurationService, NotificationService, ParkService, ParkingService, PaymentGatewayService, ThirdUsersService, TicketsService, UserService, UserboService, VehicleService];
