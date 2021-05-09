import { Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import { FormControl, Validators, FormGroup } from '@angular/forms';
import { DeleteVehiclesComponent } from '../vehicles/delete-vehicles/delete-vehicles.component';
import { MatDialog, MatDialogRef } from '@angular/material';
import { Vehicle, VehicleBusService } from '../api';
import { AuthorizationService } from '../services/authorization.service';
import { VehicleRegistrationState } from '../api/model/vehicleRegistrationState';
import { ToastService } from '../services/toast.service';
import { GuildService } from '../api/api/guild.service';
import { Guild } from '../api/model/guild';
import { TranslateService } from '@ngx-translate/core';
import { CurrentUserService } from '../services/current-user.service';
import { FUNCIONALITIES } from '../globalVariables/globalVariables';
import { DialogConsultaDGTBusComponent } from './dialog-consulta-dgt/dialog-consulta-dgt.component';
import { DialogEditVehicleBusComponent } from './dialog-edit-vehicle-bus/dialog-edit-vehicle-bus.component';
import { PaginatorService } from 'app/services/paginator.service';
import { AvailablePermission } from '../api/model/availablePermission';

@Component({
  selector: 'app-vehicles-bus',
  templateUrl: './vehicles-bus.component.html',
  styleUrls: ['./vehicles-bus.component.scss']
})
export class VehiclesBusComponent implements OnInit {

  public form: FormGroup;
  public vehicles: Vehicle[];
  public vehiclesStates: VehicleRegistrationState[];
  public loadingVehicles = false;
  public exportInProgress = false;
  public FUNCIONALITIES = FUNCIONALITIES;


  constructor(
    public curUser: CurrentUserService,
    private translate: TranslateService, private paginatorService: PaginatorService,
    private auth: AuthorizationService, private toast: ToastService,
    private _dialog: MatDialog,
    private vehicleService: VehicleBusService, public _currentUserService: CurrentUserService) {
  }

  ngOnInit() {
    this.vehicleService.vehicleStates(this.auth.getBearer()).subscribe((vehicleStates) => {
      this.vehiclesStates = vehicleStates;
      this.vehiclesStates.sort((stateA, stateB) => {
        return stateA.name > stateB.name ? 1 : -1;
      })
    });
    this.form = new FormGroup({
      matricula: new FormControl(''),
      vehicleState: new FormControl('')
    });
    this.paginatorService.init(() => {
      this.searchVehicles();
    });
    this.paginatorService.load();
    this.form.get('matricula').valueChanges.subscribe(() => {
      this.resetPaginator();
    });
    this.form.get('vehicleState').valueChanges.subscribe(() => {
      this.resetPaginator();
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

  /**
   * Exporta vehicles amb filtres actuals
   */
  public exportVehicles() {
    this.exportInProgress = true;
    this.translate.get('VEHICLES.EXPORT_IN_PROGRESS').subscribe((message) => {
      this.toast.notify('warning', message);
      this.vehicleService.vehiclesAllXls(this.auth.getBearer(),
        undefined,
        null,
        undefined,
        null,
        this.getFormValue('matricula'),
        this.getFormValue('vehicleState'))
        .subscribe((file) => {
          saveAs(file, 'Extracte vehicles ' + new Date().toLocaleDateString() + '.xls');
          this.exportInProgress = false;
        }, (err) => {
          this.toast.error(err);
          this.exportInProgress = false;
        })

    });


  }

  /**
   * Resetea el form de filtres
   */
  public clearInputs() {
    this.form.reset();
  }

  public searchVehicles() {
    this.vehicles = undefined;
    this.loadingVehicles = true;
    this.loadVehicles(
      this.paginatorService.pag.page - 1,
      this.paginatorService.pag.size,
      null,
      null,
      this.getFormValue('matricula'),
      this.getFormValue('vehicleState'));
  }


  public getFormValue(idFormControl: string) {
    const toReturn = this.form.get(idFormControl).value
    return toReturn !== '' ? toReturn : null;
  }
  /**
   * Loads vehicles with specified params
   * @param  {number} gremiId
   * @param  {string} nifGremi
   * @param  {string} matricula
   * @param  {string} vehicleStateId
   */
  private loadVehicles(page: number, size: number, gremiId: number, nifGremi: string, matricula: string, vehicleStateId: number) {
    this.vehicleService.vehiclesAll(this.auth.getBearer(), page, size, undefined, gremiId, undefined, nifGremi, matricula, vehicleStateId)
      .subscribe((vehicles) => {
        this.vehicles = vehicles.contents;
        this.paginatorService.pag.totalPages = vehicles.totalPages;
        this.loadingVehicles = false;
      }, (err) => {
        this.toast.error(err);
        this.loadingVehicles = false;
      });
  }

  public resetPaginator() {
    this.paginatorService.reset();
    this.vehicles = undefined;
  }

  /*deleteVehicle(id: number): void {
    const dialogRef = this._dialog.open(DeleteVehiclesComponent, {
      width: '600px',
      data: {
        id: id
      }
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data === 'Confirm') {
        this.vehicleService.deleteVehicle(id, this.auth.getBearer(), undefined).subscribe(() => {
          this.translate.get('VEHICLES.DELETED_CORRECTLY').subscribe((message) => {
            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
            this.searchVehicles();
          });
        }, (err) => {
          this.toast.error(err);
        })
      }
    });
  }*/

  public consultaDGT(vehicle: Vehicle): void {
    this._dialog.open(DialogConsultaDGTBusComponent, {
      width: '600px',
      data: {
        vehicleId: vehicle.vehicleId,
        plateNumber: vehicle.plateNumber
      }
    });
  }

  public modifyVehicle(vehicle: Vehicle): void {
    const dialogRef: MatDialogRef<DialogEditVehicleBusComponent> = this._dialog.open(
      DialogEditVehicleBusComponent,
      {
        width: '600px',
        data: {
          vehicle: vehicle
        }
      });

    dialogRef.afterClosed().subscribe((data) => {
      if (data.confirm) {
        vehicle.vehicleRegistrationState.vehicleRegistrationStateId = data.state;
        vehicle.vehicleType = {
          vehicleTypeId: data.type
        };
        vehicle.descriptionState = data.descState;
          this.vehicleService.updateVehicle(vehicle.vehicleId,
          this.auth.getBearer(), vehicle, vehicle.userId, undefined).subscribe((ok) => {
            vehicle.vehicleRegistrationState = ok.vehicleRegistrationState;
            this.translate.get('DIALOG_EDIT_VEHICLE.UPDATED', { plateNumber: vehicle.plateNumber }).subscribe((message) => {
              this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
            });
          }, (err) => {
            this.toast.error(err);
          })
      }
    });

  }

  
  /*consultaPermissos(vehicle: Vehicle){
    this._dialog.open(DialogConsultaDGTComponent, {
      width: '600px',
      data: {
        availablePermission: vehicle.availablePermission,
      }
    });
  }*/



  getAvailablePermission(availablePermission: AvailablePermission[]){

    var msg = '';
    var msgPermission = '';
    if(availablePermission != null && availablePermission.length > 0){
      availablePermission.forEach(element => {
        if(element.totalQuantity > -1){
          msgPermission = element.name+': '+ element.availableQuantity+'/'+element.totalQuantity;
          console.log(msg.length);
          if(msg.length==0){
            msg = msgPermission;
          }else{
            msg = msg +' '+msgPermission;
          }
        }
      });
    } 
    if(msg.length==0){
      msg = '-';
    }
    return msg;

  }

}
