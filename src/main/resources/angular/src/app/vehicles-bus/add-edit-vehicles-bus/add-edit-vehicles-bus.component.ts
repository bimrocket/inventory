import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthorizationService } from '../../services/authorization.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { VehicleBusService, Vehicle } from '../../api';
import { VehicleRegistrationState } from '../../api/model/vehicleRegistrationState';
import { LocalizedDatePipe } from '../../pipes/localizedDate.pipe';
import { VehicleDocumentation } from '../../api/model/vehicleDocumentation';
import { FileService } from '../../services/file.service';
import { ETIQUETAS_ECO } from '../../globalVariables/globalVariables';
import { PlateNumberService } from 'app/services/plateNumber.service';

@Component({
  selector: 'app-add-edit-vehicles-bus',
  templateUrl: './add-edit-vehicles-bus.component.html',
  styleUrls: ['./add-edit-vehicles-bus.component.scss']
})
export class AddEditVehiclesBusComponent implements OnInit {

  params: any;
  addingNew = true;
  form: FormGroup;
  error: any = {
    errorCode: null
  }
  public vehicle: Vehicle;
  public loadingVehicle = true;
  public vehicleStates: VehicleRegistrationState[];
  public vehicleDocumentation: VehicleDocumentation[];
  public etiquetas: String[] = ETIQUETAS_ECO;
  public stateWasChanged: Boolean = false;
  
  public vehicleAnterior: String;
  public estatAnterior: String; 
  public MarcaAnterior: String;
  public tipusVehicleAnterior: String; 
  public modelAnterior: String; 
  
  constructor(
    private router: Router, private vehicleService: VehicleBusService,
    private auth: AuthorizationService, private route: ActivatedRoute, private toast: ToastService,
    private translate: TranslateService, private localizedDate: LocalizedDatePipe, private fileService: FileService,
    private _servPlateNumber:PlateNumberService) { }

  ngOnInit() {
    this.form = new FormGroup({
      plateNumber: new FormControl('', Validators.required),
      authorization: new FormControl(null),
      car: new FormControl(''),
      etiqueta: new FormControl(''),
      descriptionState: new FormControl(''),
      state: new FormControl(2),
      international: new FormControl(false),
      registrationDate: new FormControl(''),
      closedDate: new FormControl(''),
      marca: new FormControl(''),
      model: new FormControl('')
    });

    this.form.get('etiqueta').disable();
    this.form.get('registrationDate').disable();

    this.vehicleService.vehicleStates(this.auth.getBearer()).subscribe((states) => {
      this.vehicleStates = states;
      this.vehicleStates.sort((stateA, stateB) => {
        return stateA.name > stateB.name ? 1 : -1;
      })
    });

  
    this.route.params.subscribe(params => {
      this.params = params;
      if (this.params.id != 'new') {
        this.form.get('plateNumber').disable();
        this.vehicleService.vehicle(this.params.id, this.auth.getBearer(), undefined, undefined).subscribe((vehicle) => {
          this.vehicle = vehicle;
          this.loadingVehicle = false;
          if (this.vehicle.guildMemberId) {
            this.loadingVehicle = true;
          }
          this.form.get('descriptionState').setValue(this.vehicle.descriptionState);
          this.form.get('plateNumber').setValue(this.vehicle.plateNumber);
          this.form.get('authorization').setValue(this.vehicle.vehicleType.vehicleTypeId);
          this.form.get('car').setValue(this.vehicle.alias);
          this.form.get('etiqueta').setValue(this.vehicle.ecologicalTag);
          this.form.get('state').setValue(this.vehicle.vehicleRegistrationState.vehicleRegistrationStateId);
          this.form.get('registrationDate').setValue(this.localizedDate.transform(this.vehicle.registrationDate));
          this.form.get('closedDate').setValue(this.localizedDate.transform(this.vehicle.closingDate));
          this.form.get('marca').setValue(this.vehicle.marca);
          this.form.get('model').setValue(this.vehicle.model);
    
          this.estatAnterior=this.form.get('state').value; 
          this.MarcaAnterior=this.form.get('marca').value; 
          this.vehicleAnterior=this.form.get('car').value;
          this.tipusVehicleAnterior=this.form.get('authorization').value;
          this.modelAnterior=this.form.get('model').value;
    
          this.vehicleService.getVehicleDocs(this.params.id, this.auth.getBearer()).subscribe((docs) => {
            this.vehicleDocumentation = docs;
          }, (err) => {
            this.toast.error(err);
          });
        }, (err) => {
          this.toast.error(err);
          this.loadingVehicle = false;
        });
        this.addingNew = false
      } else {
        this.loadingVehicle = false;
        this.addingNew = true;
      }
    });

  }

  stateChange(){
    this.stateWasChanged=true;
  }

  save() {
    this.loadingVehicle = true;
    this.changePlateNumber(this.form.get('plateNumber').value);
    if (this.addingNew) {
      this.vehicle = {
        plateNumber: this.form.get('plateNumber').value,
        international: this.form.get('international').value,
        guildMemberId: this.form.get('agremiat').value,
        alias: this.form.get('car').value,
      };
      this.vehicleService.createVehicle(this.auth.getBearer(), this.vehicle).subscribe((vehicle) => {
        this.translate.get('VEHICLES.CREATED_CORRECTLY').subscribe((message) => {
          this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
          this.router.navigateByUrl('/vehicles-bus');
          this.loadingVehicle = false;
        });
      }, (err) => {
        this.toast.error(err);
        this.loadingVehicle = false;
      });
    } else {
      this.vehicle.alias = this.form.get('car').value;
      this.vehicle.vehicleRegistrationState = {
        vehicleRegistrationStateId: this.form.get('state').value
      };
      this.vehicle.vehicleType = {
        vehicleTypeId: this.form.get('authorization').value
      }
      this.vehicle.marca = this.form.get('marca').value;
      this.vehicle.model = this.form.get('model').value;
      this.vehicle.descriptionState = this.form.get('descriptionState').value;
      this.vehicleService.updateVehicle(this.vehicle.vehicleId,
        this.auth.getBearer(),
        this.vehicle,
        undefined,
        undefined).subscribe(() => {
          this.translate.get('VEHICLES.UPDATED_CORRECTLY').subscribe((message) => {
            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
            this.router.navigateByUrl('/vehicles-bus');
            this.loadingVehicle = false;
          });
        }, (err) => {
          this.toast.error(err);
          this.loadingVehicle = false;
        });
    }

  }

  download(id) {
    this.vehicleDocumentation.forEach(element => {
      if (element.docId == id) {
        this.fileService.seeFile(element.fileDocument, element.fileType, element.fileName);
      }
    });
  }

  changePlateNumber(plateNumber){
    this.form.get('plateNumber').setValue(this._servPlateNumber.change(plateNumber));  
    this.valueChanged(); 
  }


   valueChanged(): boolean{
		return (
			this.estatAnterior == this.form.get('state').value &&
			this.MarcaAnterior == this.form.get('marca').value &&
			this.vehicleAnterior == this.form.get('car').value &&
			this.tipusVehicleAnterior == this.form.get('authorization').value &&
			this.modelAnterior == this.form.get('model').value
		);
	}


}
