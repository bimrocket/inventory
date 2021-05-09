import { Component, OnInit, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { VehiclesComponent } from '../vehicles.component';
import { ToastService } from '../../services/toast.service';
import { VehicleService, VehicleType, Vehicle } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { VehiclesService } from '../../api/api/vehicles.service';
import { VehicleRegistrationState } from '../../api/model/vehicleRegistrationState';

@Component({
  selector: 'app-dialog-edit-vehicle',
  templateUrl: './dialog-edit-vehicle.component.html',
  styleUrls: ['./dialog-edit-vehicle.component.scss']
})
export class DialogEditVehicleComponent implements OnInit {

  public vehicle: Vehicle;
  public loading = false;
  public vehicleType = new FormControl(undefined);
  public vehicleState = new FormControl(undefined);
  public types: Array<VehicleType>;
  public states: VehicleRegistrationState[];
  public descriptionState = new FormControl(undefined);

  constructor(
    private toast: ToastService, private vehicleService: VehicleService,
    private vehiclesService: VehiclesService, private auth: AuthorizationService,
    private thisDialogRef: MatDialogRef<DialogEditVehicleComponent>, @Inject(MAT_DIALOG_DATA) private data: any) { 
      this.vehicle = data.vehicle;
    }

  ngOnInit() {
    if (this.vehicle.vehicleType) {
      this.vehicleType.patchValue(this.vehicle.vehicleType.vehicleTypeId);
    }
    if (this.vehicle.vehicleRegistrationState) {
      this.vehicleState.patchValue(this.vehicle.vehicleRegistrationState.vehicleRegistrationStateId);
    }
    if (this.vehicle.descriptionState) {
      this.descriptionState.patchValue(this.vehicle.descriptionState);
    }
    this.loading = true;
    this.vehiclesService.vehiclesType(this.auth.getBearer()).subscribe((types) => {
      this.types = types;
      this.vehicleService.vehicleStates(this.auth.getBearer()).subscribe((states) => {
        this.states = states;

        this.loading = false;
      }, (err) => {
        this.toast.error(err);
      });
    }, (err) => {
      this.toast.error(err);
    })
  }

  public onCloseConfirm(): void {
    this.thisDialogRef.close({
      confirm : true,
      type : this.vehicleType.value,
      state : this.vehicleState.value,
      descState : this.descriptionState.value
    });
  }

  public onCloseCancel(): void {
    this.thisDialogRef.close({
      confirm : false
    });
  }

}
