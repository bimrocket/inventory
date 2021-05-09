import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { ActivatedRoute, Router } from '@angular/router';
import { UserService, VehicleService, TicketsService, ParkService, ZoneType, User, Vehicle,VehicleBusService } from '../../api';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { DetallDialogComponentComponent } from './detall-dialog-component/detall-dialog-component.component';
import { CurrentUserService } from '../../services/current-user.service';
import { TermsService } from '../../api/api/terms.service';
import { DataService } from '../../api/api/data.service';
import { FormControl, Validators } from '@angular/forms';
import { DialogConfirmComponent } from './dialog-confirm/dialog-confirm.component';
import { CercadorOperacionsService } from '../../services/cercador-operacions.service';
import { transStatuses, transTypes } from '../../../data/transactions';
import { PaginatorService } from '../../services/paginator.service';
import { ExtractesService } from '../../services/extractes.service';
import { ServiceService } from '../../api/api/service.service';
import { config } from 'config/config';
import { ActivityType } from '../../api/model/activityType';
import { FUNCIONALITIES,APLICATION } from '../../globalVariables/globalVariables';
import { SproUserStatus } from '../../api/model/sproUserStatus';



let that;

@Component({
  selector: 'app-edit-users',
  templateUrl: './edit-users.component.html',
  styleUrls: ['./edit-users.component.scss']
})
export class EditUsersComponent implements OnInit {

  public params: any;
  public user: any = {
    permissions: {},
    vehicles: [],
    services: [],
    creditcard:{},
    denuncies:[]
  } as any;
  public selectorUsuaris = new FormControl('', [Validators.required]);
  public tickets: any = undefined;
  public fleetUsers: any = [];
  public transFilter = {
    selectedDay: undefined,
    fromDate: undefined,
    toDate: undefined,
    transTypeIds: [],
    transStatusIds: [],
    tickets: ''
  };
  public matricules = [];
  public showDesde = false;
  public showTransDesde = false;
  public ppPlusPlus = false;
  public ppPlus = false;
  public ppBasic = false;
  public exportInProgress = false;
  public conditionsUse = false;
  public userMustReview = false;
  public ambConditions = {
    ppPlusPlus: false,
    ppPlus: false,
    ppBasic: false,
    conditionsUse: false,
    userMustReview: false
  };
  public transactions = undefined;
  public transTypes = transTypes;
  public transStatuses = transStatuses;
  public loadingUser = false;
  public loadingTrans = false;
  public loadingVehicles = false;
  public loadingDadesBancaries = false;
  public loadingDenuncies = false;
  public ticketTypes;
  public zoneTypes;
  public cities;
  public services;
  public activityTypes: ActivityType[];
  public FUNCIONALITIES = FUNCIONALITIES;
  public userStatus:SproUserStatus= {} as any;

  public isDum = false;

  constructor(
    private serviceService: ServiceService,
    public paginator: PaginatorService, public extractes: ExtractesService,
    private router: Router, public cercador: CercadorOperacionsService,
    public _currentUserService: CurrentUserService, private _authService: AuthorizationService, private route: ActivatedRoute,
    private _userService: UserService, private _vehicleService: VehicleService, private _vehicleBusService: VehicleBusService, private _toast: ToastService,
    private translate: TranslateService, private _dialog: MatDialog, private _ticketService: TicketsService,
    private _termsService: TermsService, private data: DataService, private park: ParkService) {
    that = this;
    this.route.params.subscribe(params => {
      that.params = params;
      this.data.activityTypeGet(config.LOCALE).subscribe((activityTypes) => {
        this.activityTypes = activityTypes;
      });
      this.loadUser();
      this.cercador.init();
    });
    
  }



  validateUser() {
    this.translate.get('EDIT_USUARIS.VALIDATE_USER').subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
        }
      });
    });
  }

  getAmount(trans) {
    let setNegative = false;
    let amount = trans.currentAmount ? trans.currentAmount : trans.amount;
    if (amount == null) {
      amount = 0;
    }
    amount = parseInt(amount, 10);
    if (amount < 0) {
      setNegative = true;
      amount = amount * -1;
    }
    const euros = Math.floor(amount / 100);
    const centims = amount % 100 + '€';
    return (setNegative ? '-' : '') + euros + '.' + (centims.length === 2 ? '0' + centims : centims);
  }

  getOperationType(trans) {
    const type = trans.transactionType.transactionTypeId;
    const operationType = transTypes.find((item) => {
      return item.id === type;
    });
    return operationType !== undefined ? operationType.description : 'EDIT_USUARIS.TYPE_UNKNOWN';
  }

  getMovType(trans) {
    const status = trans.status ? trans.status.transactionStatusId : trans.transactionStatus.transactionStatusId;
    /*switch (status) {
      case 1: return 'EDIT_USUARIS.STATUS_AWAITING_RESPONSE';
      case 2: return 'EDIT_USUARIS.STATUS_AUTH';
      case 3: return 'EDIT_USUARIS.STATUS_SETTLED';
      case 4: return 'EDIT_USUARIS.STATUS_REFUND';
      case 5: return 'EDIT_USUARIS.STATUS_VOID';
      case 10: return 'EDIT_USUARIS.STATUS_ERROR';
      case 11: return 'EDIT_USUARIS.STATUS_DENIED_TRANS';
      case 12: return 'EDIT_USUARIS.STATUS_REJECTED';
      case 13: return 'EDIT_USUARIS.STATUS_LOST';
      case 14: return 'EDIT_USUARIS.STATUS_ERROR_BANK';
      case 15: return 'EDIT_USUARIS.STATUS_PARSING';
      case 16: return 'EDIT_USUARIS.STATUS_DISABLED';
      default: return 'EDIT_USUARIS.STATUS_UNKNOWN';
    }*/
    return 'EDIT_USUARIS.STATUS_' + status;

  }

  getFirstDate(trans) {
    let lowest = new Date('2999-09-19T11:19:05.000Z');
    trans.history.forEach(history => {
      const actualDate = new Date(history.historyDate);
      if (actualDate < lowest) {
        lowest = actualDate;
      }
    });
    return lowest;
  }

  isActiveService(serviceId: number) {
    return this.user.services.find((item) => {
      return item.serviceId === serviceId;
    })
  }

  loadUser() {
    this.loadingUser = true;
    this._userService.user(this.params.id, this._authService.getBearer()).subscribe((data) => {
      this.user = data;
      this.isDum = this.user.applicationType == APLICATION.ZONA_DUM
      this.user.dateAdded = new Date(this.user.dateAdded);
      if (this.user.creditcard != null) {
        const date = new Date(this.user.creditcard.expirationDate);
        this.user.creditcard.expirationDate = (date.getMonth() + 1) + '/' + (date.getFullYear() + '').substring(2, 4);
      }

      this._userService.userStatus(this.user.userId, this._authService.getBearer()).subscribe((userStatus) => {
        this.userStatus = userStatus;
      });
 
      this.paginator.init(() => {
        this.loadingVehicles = true;
        if(this.user.applicationType == APLICATION.ZONA_DUM){
          this._vehicleService.vehiclesAll(this._authService.getBearer(),this.paginator.pag.page - 1,
          this.paginator.pag.size, this.user.userId).subscribe((vehicles) => {
            this.user.vehicles = vehicles.contents;
            this.paginator.pag.totalPages = vehicles.totalPages;
            this.matricules = Array.from(new Set(this.user.vehicles.map((item: any) => item.plateNumber)));
            this.loadingVehicles = false;
          });
        }else{
          this._vehicleBusService.vehiclesAll(this._authService.getBearer(),this.paginator.pag.page - 1,
          this.paginator.pag.size, this.user.userId).subscribe((vehicles) => {
            this.user.vehicles = vehicles.contents;
            this.paginator.pag.totalPages = vehicles.totalPages;
            this.matricules = Array.from(new Set(this.user.vehicles.map((item: any) => item.plateNumber)));
            this.loadingVehicles = false;
          });
        }
      });
      this.paginator.pag.size = 100;
      this.paginator.load();

      //TODO CARGAR DENUNCIES DEL USUARI
      this.user.denuncies = [];

      this._userService.getUserServices(this._authService.getBearer(), this.user.userId).subscribe((services) => {
        this.user.services = services;
      });
      this.serviceService.getServices(config.LOCALE).subscribe((services) => {
        this.services = services;
      });
      this._termsService.getTerms(this.user.userId, this._authService.getBearer(), 'dum').subscribe((terms) => {
        this.conditionsUse = terms.currentServiceCondition != null;
        this.userMustReview = terms.userMustReview;
        if (terms.currentPrivacyPolicy !== 0) {
          this._termsService.getPrivacyPolicyById(terms.currentPrivacyPolicy).subscribe((privacyPolicy) => {
            if (privacyPolicy.type.privacyPolicyType === 3) {
              this.ppPlusPlus = true;
              this.ppPlus = true;
              this.ppBasic = true;
            } else if (privacyPolicy.type.privacyPolicyType === 2) {
              this.ppPlus = true;
              this.ppBasic = true;
            } else if (privacyPolicy.type.privacyPolicyType === 1) {
              this.ppBasic = true;
            }
          });
        }
        this.loadingUser = false;
      }, (err) => {
        this._toast.error(err);
        this.loadingUser = false;
      });
      if (this.user.accountType === 'U') {
        this.fleetUsers = [this.user];
        this.cercador.filter.users = [this.user.userId];
      } else {
        this._userService.fleetUsers(this.user.accountId, this._authService.getBearer()).subscribe((fleetUsers) => {
          this.fleetUsers = fleetUsers;
          let adminUser: User;
          fleetUsers.forEach((item) => {
            if (item.permissions.isAdmin) {
              adminUser = item;
            }
          });
          if (adminUser) {
            this.cercador.filter.users = [adminUser.userId + ''];
            this.selectorUsuaris.setValue([adminUser.userId + '']);
          } else {
            this.cercador.filter.users = [this.user.userId];
            this.selectorUsuaris.setValue([this.user.userId]);
          }

        }, (err) => {
          this._toast.error(err);
        });
      }
    });
  }
  
  /**
   * Retorna el estat del usuari
   * @param  {User} user Usuari a comprova
   * @returns string El estat
   */
  public getEstatUsuari(user: User): string {
    if (user) {
      switch (user.userStateId) {
        case 1:
          return 'Pendent activació';
        case 2:
          return 'Actiu';
        case 3:
          return 'Bloquejat';
        case 4:
          return 'Error enviant correu de registre';
        case 5:
          return 'Eliminat';
        default:
          return 'ERROR: Estat desconegut';
      }
    }

  }

  deleteUser(user) {
    this.translate.get('EDIT_USUARIS.DELETE_USER').subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
          this._userService.deleteUser(user.userId, this._authService.getBearer()).subscribe((ok) => {
            this.translate.get('EDIT_USUARIS.USER_DELETED').subscribe((message2) => {
              this._toast.notify('success', message2);
              this.router.navigate(['/gestio-operativa']);
            });
          }, (err) => {
            this._toast.error(err);
          })
        }
      });
    });
  }

  logoutUser() {
    this.translate.get('EDIT_USUARIS.CLOSE_SESSION').subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
          this._userService.logoutUser(this.user.userId, this._authService.getBearer()).subscribe((ok) => {
            this.translate.get('EDIT_USUARIS.USER_LOGOUT').subscribe((message2) => {
              this._toast.notify('success', message2);
            });
          }, (err) => {
            this._toast.error(err);
          })
        }
      });
    });
  }

  unlockUser() {
    this.translate.get('EDIT_USUARIS.RESET_COMPT').subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
          this._userService.unlockUser(this.user.userId, this._authService.getBearer()).subscribe((ok) => {
            this.translate.get('EDIT_USUARIS.USER_UNLOCKED').subscribe((message2) => {
              this._toast.notify('success', message2);
              this.loadUser();
            });
          }, (err) => {
            this._toast.error(err);
          })
        }
      });
    });
  }

  toggleUserBlock(userId, userStateId) {
    const body = {
      block: false
    };
    let text = 'EDIT_USUARIS.BLOCK_USER';
    if (userStateId === 2) {
      body.block = true;
    } else if (userStateId === 3) {
      body.block = false;
      text = 'EDIT_USUARIS.UNBLOCK_USER'
    }
    this.translate.get(text).subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
          this._userService.blockUser(userId, this._authService.getBearer(), body).subscribe(() => {
            this.loadUser();
          }, (err) => {
            this._toast.error(err);
          });
        }
      });
    });
  }

  sendRecover(validation: boolean): void {
    const missatge = validation ? 'EDIT_USUARIS.VALIDATE_USER' : 'EDIT_USUARIS.SEND_RECOVER';
    this.translate.get(missatge).subscribe((message) => {
      const dialogRef = this._dialog.open(DialogConfirmComponent, {
        width: '600px',
        data: {
          action: message
        }
      });
      dialogRef.afterClosed().subscribe((data) => {
        if (data === 'Confirm') {
          this._userService.resetPasswordInfo(
            this.user.email, 'app', true, this._authService.getBearer(), 'response', false).subscribe((data2) => {
              if (validation) {
                this.translate.get('EDIT_USUARIS.EMAIL_VALIDATION_SENT').subscribe((message4) => {
                  this._toast.notify('success', message4);
                });
              } else {

                this.translate.get('EDIT_USUARIS.EMAIL_SENT').subscribe((message3) => {
                  this._toast.notify('success', message3);
                });
              }
            }, (err) => {
              this._toast.error(err);
            });
        }
      });
    });
  }

  openDetalls(user): void {
    const dialogRef = this._dialog.open(DetallDialogComponentComponent, {
      width: '600px',
      data: {
        user: user
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this._userService.user(this.params.id, this._authService.getBearer()).subscribe((user2) => {
          this.user = user2;
          this.user.dateAdded = new Date(this.user.dateAdded);
          if (this.user.creditcard != null) {
            this.user.creditcard.expirationDate = new Date(this.user.creditcard.expirationDate);
          }
          this.paginator.load();
        });
      }
    });
  }

  getColorState(vehicle: Vehicle) {
    const colorStyle = { background: 'grey', color: 'white' };
    if (vehicle.vehicleRegistrationState) {
      switch (vehicle.vehicleRegistrationState.vehicleRegistrationStateId) {
        case 1: case 5:
          colorStyle.background = 'green';
          break;
        case 2: 
          colorStyle.background = 'yellow';
          colorStyle.color = 'black';
          break;
        case 3: 
          colorStyle.background = '#d1ecf1';
          colorStyle.color = '#0c5460';
          break;
        case 4:
          colorStyle.background = 'red';
          break;
        default:
          colorStyle.background = 'grey';
          break;

      }
    }
    return colorStyle;
  }

  getActivitatCompte(): string {
    if (this.activityTypes === undefined) {
      return '-';
    } else {
      let selectedActivity: ActivityType = null;
      this.activityTypes.forEach((item) => {
        if (item.activityTypeId === this.user.activityTypeId) {
          selectedActivity = item;
        }
      });
      return selectedActivity !== null ? selectedActivity.activityTypename : '-';
    }
  }

  ngOnInit() {
  }

}
