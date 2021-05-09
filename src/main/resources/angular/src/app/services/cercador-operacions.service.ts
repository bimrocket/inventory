// src/app/auth/token.interceptor.ts
import { Injectable } from '@angular/core';
import {
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { AuthorizationService } from './authorization.service';
import { from, Subscription } from 'rxjs';
import { UserService, Ticket, TicketsService, ZoneType, JuridicEntity, ParkingService, ParkService, GuildService } from '../api';
import { Router } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import * as moment from 'moment';
import { saveAs } from 'file-saver';
import { config } from '../../config/config';
import { ToastService } from './toast.service';
import { DataService } from '../api/api/data.service';
import { TicketType } from '../api/model/ticketType';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog } from '@angular/material';
import { RefundTicketDialogComponent } from '../gestio-operativa/edit-users/refund-ticket-dialog/refund-ticket-dialog.component';
import { DIalogStopTicketComponent } from '../gestio-operativa/edit-users/dialog-stop-ticket/dialog-stop-ticket.component';
import { DialogPosicioTiquetComponent } from '../cercador-operacions/dialog-posicio-tiquet/dialog-posicio-tiquet.component';
import { CurrentUserService } from './current-user.service';
import { Guild } from '../api/model/guild';
import { EstatsUsuari, FUNCIONALITIES } from '../globalVariables/globalVariables';
import { PlateNumberService } from 'app/services/plateNumber.service';
export interface FilterCercador {
    selectedDay: FilterDay,
    matriculesArray?: Array<string>,
    matricules?: string,
    users?: Array<string>,
    tickets?: string,
    typeMovements: Array<number>,
    zoneTypes: Array<string>,
    cityIds: Array<number>,
    fromDate: Date,
    toDate: Date,
    thirdUserId?: number,
    typeDate: string,
    gremi?: string,
    userStateId?: string,
    nif?: string,
    telefon?: string,
    email?: string,
}

export interface FilterDay {
    fromDate: Date,
    toDate: Date,
    name: string
}
@Injectable()
export class CercadorOperacionsService {

    public days: Array<FilterDay>;
    public typeDates: Array<string>;
    public filter: FilterCercador;
    public tickets: Array<Ticket> = [];
    public loadingTickets = false;
    public exportInProgress = false;
    public ticketTypes: Array<TicketType>;
    public zoneTypes: Array<ZoneType>;
    public cities: Array<JuridicEntity>;
    public citiesFiltrats: Array<JuridicEntity>;
    public gremis: Array<Guild>;
    public maxTickets = config.MAX_TICKETS;
    public loadPetitionInProgress: Subscription = null;
    public exportPetitionInProgress: Subscription = null;
    public initialized = false;
    public mostrarAlertaMaxTiquets: boolean;
    public mostrarAlertaNumTiquets: boolean;
    public numTiquets: number;
    public estatsUsuari = EstatsUsuari;

    constructor(
        private guilds: GuildService,  private _servPlateNumber:PlateNumberService,
        private park: ParkService, private currentUser: CurrentUserService,
        private translate: TranslateService, private dialog: MatDialog,
        private toast: ToastService, private data: DataService,
        private auth: AuthorizationService, private ticketsService: TicketsService) {
        this.data.getAllTicketTypes(this.auth.getBearer()).subscribe((ticketTypes) => {
            this.ticketTypes = ticketTypes;
            this.ticketTypes.forEach((ticketType) => {
                this.filter.typeMovements.push(ticketType.ticketTypeId);
            });
        });
        this.guilds.guildGet(this.auth.getBearer()).subscribe((gremis) => {
            this.gremis = gremis;
        });
        this.data.getAllZoneTypes(this.auth.getBearer()).subscribe((zoneTypes) => {
            this.zoneTypes = zoneTypes;
        });
        this.data.getCities(true).subscribe((cities) => {
            this.cities = cities.filter(citi => citi.nif != 'P1111111Z');
            this.citiesFiltrats = cities.filter(citi => citi.nif != 'P1111111Z');
        });
    }

    public init() {
        this.typeDates = [
            'CERCADOR.DATA_INICI',
            'CERCADOR.DATA_FI'
        ];
        this.days = [
            {
                fromDate: null,
                toDate: null, // counts as actual date and hour
                name: 'CERCADOR.ANT_PERS'
            },
            {
                fromDate: moment().toDate(),
                toDate: moment().toDate(), // counts as actual date and hour
                name: 'CERCADOR.LAST_24_HOURS'
            },
            {
                fromDate: moment().subtract(7, 'days').toDate(),
                toDate: moment().toDate(),
                name: 'CERCADOR.LAST_7_DAYS' // Default
            },
            {
                fromDate: moment().subtract(30, 'days').toDate(),
                toDate: moment().toDate(),
                name: 'CERCADOR.LAST_30_DAYS'
            }
        ];
        this.filter = {
            selectedDay: this.days[2],
            fromDate: this.days[2].fromDate,
            toDate: this.days[2].toDate,
            typeMovements: [],
            zoneTypes: [],
            cityIds: [],
            typeDate: this.typeDates[0],
        };
        if (this.ticketTypes) {
            this.ticketTypes.forEach((ticketType) => {
                this.filter.typeMovements.push(ticketType.ticketTypeId);
            });
        }
        this.tickets = undefined;
        this.initialized = true;
        this.loadingTickets = false;
        this.exportInProgress = false;
        this.resetLoadPetition();
        this.resetExportPetition();

        this.mostrarAlertaMaxTiquets = false;
        this.mostrarAlertaNumTiquets = false;
    }

    public userComparator(a, b) {
        return a == b;
    }

    public dateChanged(type, $event) {
        if ($event.value) {
            this.filter[type] = $event.value;
        }
    }

    public antiguetatChanged($event): void {
        console.log
        if ($event.value && $event.value.fromDate != null && $event.value.toDate != null) {
            this.filter.fromDate = $event.value.fromDate;
            this.filter.toDate = $event.value.toDate;
        }
    }

    public changeDate(): void {
        this.filter.selectedDay = this.days[0]
    }

    public exportTickets(format: string) {
        this.translate.get('GENERAL.EXPORT_IN_PROGRESS').subscribe((message) => {
            this.toast.notify('warning', message);
        });
        this.exportInProgress = true;
        const fromDate = moment(this.filter.fromDate).startOf('day').toDate();
        const toDate = moment(this.filter.toDate).endOf('day').toDate();
        let matricules = undefined;
        if (this.filter.matricules) {
            matricules = this.splitString(this.filter.matricules);
        } else {
            matricules = this.filter.matriculesArray;
        }
        let tickets = undefined;
        if (this.filter.tickets) {
            tickets = this.splitString(this.filter.tickets);
        }
        let startDate, endDate, startDateMin, startDateMax, endDateMin, endDateMax;
        if (this.filter.typeDate === this.typeDates[0]) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = fromDate;
            startDateMax = toDate;
            endDateMin = undefined;
            endDateMax = undefined;
        } else if (this.filter.typeDate === this.typeDates[1]) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = undefined;
            startDateMax = undefined;
            endDateMin = fromDate;
            endDateMax = toDate;
        }

        let zoneTypes = this.filter.zoneTypes;
        let users = this.filter.users;
        let typeMovements = this.filter.typeMovements;
        const thirdUserId = this.filter.thirdUserId;
        let cityIds = this.filter.cityIds;

        let email = this.filter.email;
        let userStateId = this.filter.userStateId;
        let nif = this.filter.nif;
        let telefon = this.filter.telefon;
        let gremi = this.filter.gremi;

        if (tickets) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = undefined;
            startDateMax = undefined;
            endDateMin = undefined;
            endDateMax = undefined;
            zoneTypes = undefined;
            matricules = undefined;
            users = undefined;
            typeMovements = undefined;
            cityIds = undefined;
            email = undefined;
            userStateId = undefined;
            nif = undefined;
            telefon = undefined;
            gremi = undefined;
        }

        if (!cityIds || cityIds.length === 0) {
            cityIds = [];
            this.citiesFiltrats.forEach((item) => {
                cityIds.push(item.juridicId);
            });
        }

        this.resetExportPetition();
        if (format === 'pdf') {
            this.exportPetitionInProgress = this.ticketsService.ticketsPdf(this.auth.getBearer(),
                startDate, endDate, zoneTypes, matricules,
                users, typeMovements, this.maxTickets, tickets, thirdUserId,
                cityIds, startDateMin, startDateMax,
                endDateMin, endDateMax, email, userStateId, nif, telefon, gremi, 'response').subscribe((excel) => {
                    if (parseFloat(excel.headers.get('X-ROWS')) >= this.maxTickets) {
                        this.translate.get('CERCADOR.ALERTA_EXPORT_MAX_TIQUETS').subscribe((message1) => {
                            this.translate.get('CERCADOR.ALERTA_EXPORT_MAX_TIQUETS_2').subscribe((message2) => {
                                this.toast.notify('warning', message1 + ' ' + this.maxTickets + ' ' + message2);
                            });
                        })
                    }
                    saveAs(excel.body, 'Tickets.pdf');
                }, (err) => {
                    this.toast.error(err);
                    this.exportInProgress = false;
                    this.exportPetitionInProgress = null;
                }, () => {
                    this.exportInProgress = false;
                    this.exportPetitionInProgress = null;
                });
        } else if (format === 'excel') {
            this.exportPetitionInProgress = this.ticketsService.ticketsExcel(this.auth.getBearer(),
                startDate, endDate, zoneTypes, matricules,
                users, typeMovements, this.maxTickets, tickets, thirdUserId,
                cityIds, startDateMin, startDateMax,
                endDateMin, endDateMax, email, userStateId, nif, telefon, gremi, 'response').subscribe((excel) => {
                    if (parseFloat(excel.headers.get('X-ROWS')) >= this.maxTickets) {
                        this.translate.get('CERCADOR.ALERTA_EXPORT_MAX_TIQUETS').subscribe((message1) => {
                            this.translate.get('CERCADOR.ALERTA_EXPORT_MAX_TIQUETS_2').subscribe((message2) => {
                                this.toast.notify('warning', message1 + ' ' + this.maxTickets + ' ' + message2);
                            });
                        })
                    }
                    saveAs(excel.body, 'Tickets.xls');
                }, (err) => {
                    this.toast.error(err);
                    this.exportInProgress = false;
                    this.exportPetitionInProgress = null;
                }, () => {
                    this.exportInProgress = false;
                    this.exportPetitionInProgress = null;
                });
        }

    }


    public getZoneTypeName(zoneType: ZoneType) {
        if (!zoneType || zoneType.zoneTypeId === -1 || !this.zoneTypes) {
            return '';
        }
        const zoneTypeWithDesc = this.zoneTypes.find((item) => {
            return item.zoneTypeId === zoneType.zoneTypeId;
        });
        return zoneTypeWithDesc && zoneTypeWithDesc.description != null ? zoneTypeWithDesc.description.cat : '';
    }

    public searchTickets() {
        if(this.filter.fromDate === null || this.filter.toDate === null){
         return;   
        }
        this.mostrarAlertaMaxTiquets = false;
        this.mostrarAlertaNumTiquets = false;
        this.tickets = undefined;
        this.loadingTickets = true;
        const today = moment();
        const fromDate: Date = moment(this.filter.fromDate).startOf('day').toDate();
        const toDate: Date = moment(this.filter.toDate).endOf('day').toDate();
        let matricules = undefined;
        if (this.filter.matricules) {
            matricules = this.splitString(this.filter.matricules);
        } else {
            matricules = this.filter.matriculesArray;
        }
        let tickets = undefined;
        if (this.filter.tickets) {
            tickets = this.splitString(this.filter.tickets);
        }

        let startDate, endDate, startDateMin, startDateMax, endDateMin, endDateMax;
        if (this.filter.typeDate === this.typeDates[0]) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = fromDate;
            startDateMax = toDate;
            endDateMin = undefined;
            endDateMax = undefined;
        } else if (this.filter.typeDate === this.typeDates[1]) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = undefined;
            startDateMax = undefined;
            endDateMin = fromDate;
            endDateMax = toDate;
        }

        this.resetLoadPetition();

        let zoneTypes = this.filter.zoneTypes;
        let users = this.filter.users;
        let typeMovements = this.filter.typeMovements;
        const thirdUserId = this.filter.thirdUserId;
        let cityIds = this.filter.cityIds;

        let email = this.filter.email;
        let userStateId = this.filter.userStateId;
        let nif = this.filter.nif;
        let telefon = this.filter.telefon;
        let gremi = this.filter.gremi;

        if (tickets) {
            startDate = undefined;
            endDate = undefined;
            startDateMin = undefined;
            startDateMax = undefined;
            endDateMin = undefined;
            endDateMax = undefined;
            zoneTypes = undefined;
            matricules = undefined;
            users = undefined;
            typeMovements = undefined;
            cityIds = undefined;
            email = undefined;
            userStateId = undefined;
            nif = undefined;
            telefon = undefined;
            gremi = undefined;
        }

        if (!cityIds || cityIds.length === 0) {
            cityIds = [];
            this.citiesFiltrats.forEach((item) => {
                cityIds.push(item.juridicId);
            });
        }
        this.loadPetitionInProgress = this.ticketsService.getAllTickets(
            startDate, this.auth.getBearer(), this.maxTickets, null, endDate, startDateMin, startDateMax,
            endDateMin, endDateMax, tickets, matricules, users, null, cityIds,
            zoneTypes, null, email, userStateId, nif, telefon, gremi).subscribe((data) => {

                if (data.length > 0) {
                    this.numTiquets = data.length;
                    this.mostrarAlertaNumTiquets = true;
                }
                // if per alertar a l'usuari que només es mostra this.maxTickets tiquets.
                if (data.length >= this.maxTickets) {
                    this.mostrarAlertaMaxTiquets = true;
                }

                this.tickets = data;
                this.tickets.forEach(ticket => {                  
                    ticket.parkingTime = Math.floor(parseFloat(ticket.parkingTime) / 3600) + 'h ' +
                        Math.floor(
                            ((parseFloat(ticket.parkingTime) - Math.floor(parseFloat(ticket.parkingTime) / 3600) * 3600) / 60)) + 'm';
                });
                this.tickets.sort(this.sortTickets);
            }, (err) => {
                this.toast.error(err);
                this.loadingTickets = false;
                this.loadPetitionInProgress = null;
            }, () => {
                this.loadingTickets = false;
                this.loadPetitionInProgress = null;
            });
    }

    private resetLoadPetition() {
        if (this.loadPetitionInProgress !== null) {
            this.loadPetitionInProgress.unsubscribe();
            this.loadPetitionInProgress = null;
        }
    }

    private resetExportPetition() {
        if (this.exportPetitionInProgress !== null) {
            this.exportPetitionInProgress.unsubscribe();
            this.exportPetitionInProgress = null;
        }
    }

    private sortTickets(ticketA, ticketB) {
        if (new Date(ticketA.startDate) < new Date(ticketB.startDate)) {
            return 1;
        } else if (new Date(ticketA.startDate) > new Date(ticketB.startDate)) {
            return -1;
        } else {
            return 0;
        }
    }

    public selectorMunicipisDisabled(): boolean {
        return false;
    }

    public getPdf(ticketId) {
        this.translate.get('CERCADOR.DOWNLOAD_IN_PROGRESS').subscribe((message) => {
            this.toast.notify('warning', message);
        });
        this.ticketsService.ticket(ticketId, this.auth.getBearer(), 'response').subscribe((pdf) => {
            saveAs(pdf.body, 'Ticket ' + ticketId + '.pdf');
        }, (err) => {
            this.toast.error(err);
        });
    }

    public refundTicket(ticketId) {
        const dialogRef = this.dialog.open(RefundTicketDialogComponent, {
            width: '600px',
            data: {
                ticketId: ticketId
            }
        });
        dialogRef.afterClosed().subscribe((data) => {
            if (data === 'Confirm') {
            }
        });
    }

    public stopTicket(ticketId) {
        const dialogRef = this.dialog.open(DIalogStopTicketComponent, {
            width: '600px',
            data: {
                ticketId: ticketId
            }
        });
        dialogRef.afterClosed().subscribe((data) => {
            if (data === 'Confirm') {
                this.park.stop(this.auth.getBearer(), {
                    lng: 0,
                    lat: 0,
                    ticketId: ticketId,
                    endTime: new Date()
                }).subscribe(() => {
                    this.translate.get('EDIT_USUARIS.STOP_OK').subscribe((message) => {
                        this.toast.notify('success', message);
                    });
                    this.searchTickets();
                }, (err) => {
                    this.toast.error(err);
                });
            }
        });
    }

    public openMap(ticket) {
        const dialogRef = this.dialog.open(DialogPosicioTiquetComponent, {
            width: '600px',
            data: {
                ticket: ticket
            }
        });
    }


    public async payPending(ticketId):Promise<any>{
        this.translate.get('MOV.TICKET_PAYMENT_WARNING').subscribe((message) => {
            this.toast.notify('warning', message);
        });
        try{
          await Promise.all([this.sendPendingPay(ticketId)])
        }catch (err){
            this.toast.error(err);
        } finally{
          this.searchTickets();
        }

      }
    
      /**
       * Promise Pending PAY
       */
      private sendPendingPay(tiquetId): Promise<any> {
        return new Promise((resolve, reject) => {
          this.ticketsService.payTicket(
            tiquetId, this.auth.getBearer()
            ).subscribe(
              (ret: any) => resolve(ret),
              (error: any) => reject(error)
            )
        });
      }

    public canStop(ticket: Ticket) {
        return !ticket.stopDate && ticket.ticketType !== 3
            && this.currentUser.hasPermission(FUNCIONALITIES.OPERATIONS_MANAGEMENT);
    }

    public canRefund(ticket: Ticket) {
        return ticket.stopDate && ticket.ticketType !== 3 && ticket.ticketType !== 2;
    }

    public canShowMap(ticket: Ticket) {
        return ticket.ticketType === 1;
    }

    public getTipusMoviement(ticket) {
        let ticketType = null;
        this.ticketTypes.forEach((element, index) => {
            if (element.ticketTypeId === ticket.ticketType) {
                ticketType = element;
            }
        });
        return ticketType.description;
    }

    private splitString(text: string) {
        return text.trim().replace(/\s/g, '').toUpperCase().split(',');
    }


    public clearInputs() {
        this.init();
    }

    changePlateNumber(){
        if(this.filter.matricules === undefined) return;
        this.filter.matricules = this._servPlateNumber.change(this.filter.matricules);
    }

}
