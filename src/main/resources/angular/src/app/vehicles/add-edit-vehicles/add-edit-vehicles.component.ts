import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthorizationService } from '../../services/authorization.service';
import { GremisService } from '../../api/api/gremis.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { GuildService } from '../../api/api/guild.service';
import { Guild } from '../../api/model/guild';
import { VehicleService, Vehicle } from '../../api';
import { GuildMemberService } from '../../api/api/guildMember.service';
import { GuildMember } from '../../api/model/guildMember';
import { VehicleRegistrationState } from '../../api/model/vehicleRegistrationState';
import { LocalizedDatePipe } from '../../pipes/localizedDate.pipe';
import { VehicleDocumentation } from '../../api/model/vehicleDocumentation';
import { FileService } from '../../services/file.service';
import { ETIQUETAS_ECO } from '../../globalVariables/globalVariables';
import { PlateNumberService } from 'app/services/plateNumber.service';

@Component({
  selector: 'app-add-edit-vehicles',
  templateUrl: './add-edit-vehicles.component.html',
  styleUrls: ['./add-edit-vehicles.component.scss']
})
export class AddEditVehiclesComponent implements OnInit {

  params: any;
  addingNew = true;
  form: FormGroup;
  error: any = {
    errorCode: null
  }
  public vehicle: Vehicle;
  public gremis: Guild[];
  public agremiats: GuildMember[];
  public loadingVehicle = true;
  public vehicleStates: VehicleRegistrationState[];
  public errorAgremiats = false;
  public vehicleDocumentation: VehicleDocumentation[];
  public etiquetas: String[] = ETIQUETAS_ECO;
  public stateWasChanged: Boolean = false;
  
  public vehicleAnterior: String;
  public gremiAnterior: String;
  public estatAnterior: String; 
  public MarcaAnterior: String;
  public tipusVehicleAnterior: String; 
  public modelAnterior: String; 
  public agremiatAnterior: String;
  
  constructor(
    private router: Router, private vehicleService: VehicleService,
    private guildService: GuildService, private gmService: GuildMemberService,
    private auth: AuthorizationService, private route: ActivatedRoute, private toast: ToastService,
    private translate: TranslateService, private localizedDate: LocalizedDatePipe, private fileService: FileService,
    private _servPlateNumber:PlateNumberService) { }

  ngOnInit() {
    this.form = new FormGroup({
      gremi: new FormControl(null),
      plateNumber: new FormControl('', Validators.required),
      agremiat: new FormControl(null),
      authorization: new FormControl(null),
      car: new FormControl(''),
      etiqueta: new FormControl(''),
      descriptionState: new FormControl(''),
      state: new FormControl(2),
      permissionsDUM: new FormControl(false),
      permissionsBSM: new FormControl(false),
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

    this.guildService.guildGet(this.auth.getBearer()).subscribe((gremis) => {
      this.gremis = gremis;
      this.route.params.subscribe(params => {
        this.params = params;
        if (this.params.id != 'new') {
          this.form.get('plateNumber').disable();
          this.vehicleService.vehicle(this.params.id, this.auth.getBearer(), undefined, undefined).subscribe((vehicle) => {
            this.vehicle = vehicle;
            this.loadingVehicle = false;
            if (this.vehicle.guildMemberId) {
              this.loadingVehicle = true;
              this.gmService.guildMemberDetailed(this.vehicle.guildMemberId, this.auth.getBearer()).subscribe((gm) => {
                this.form.get('gremi').setValue(gm.guildId);
				this.gremiAnterior=this.form.get('gremi').value;

                this.gremiChanged(null);
                this.form.get('agremiat').setValue(gm.memberId);
				this.agremiatAnterior=this.form.get('agremiat').value;
                this.loadingVehicle = false;
              }, (err) => {
                this.toast.error(err);
                this.loadingVehicle = false;
              });
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
    }, (err) => {
      this.toast.error(err);
    });

  }

  stateChange(){
    /*if (this.form.get('state').value != 5 && this.vehicle.vehicleRegistrationState.vehicleRegistrationStateId == 5) {
      this.form.get('gremi').setValue(undefined);
      this.form.get('agremiat').setValue(undefined);
      this.form.get('permissionsDUM').patchValue(false);
      this.form.get('permissionsBSM').patchValue(false);
    }*/
    this.stateWasChanged=true;
  }
  gremiChanged(value) {
    if (this.form.get('gremi').value) {
      const selectedGremi = this.gremis.find((item) => {
        return item.gremiId == this.form.get('gremi').value;
      });
      this.form.get('agremiat').setValue(undefined);
      this.form.get('permissionsDUM').patchValue(selectedGremi.dumGranted);
      this.form.get('permissionsBSM').patchValue(selectedGremi.parkingBsmGranted);
      this.gmService.guildMembersByGuild(selectedGremi.gremiId, this.auth.getBearer())
        .subscribe((agremiats) => {
          this.form.get('agremiat').setValue(this.vehicle.guildMemberId);
          this.agremiats = agremiats;
          this.errorAgremiats = false;
		  this.valueChanged();
        }, (err) => {
          this.errorAgremiats = true;
          this.translate.get('VEHICLES.ERROR_AGREMIATS').subscribe((message) => {
            this.toast.showErrorToast(message);
          });
        })
      

    } else {
      this.changeStateVehicle();
    }

  }

  agremiatChanged(){
    if(!this.form.get('agremiat').value){
      this.changeStateVehicle();
    }
  }

  changeStateVehicle(){    
    this.form.get('gremi').setValue(undefined);
    this.form.get('agremiat').setValue(undefined);
    this.form.get('permissionsDUM').patchValue(false);
    this.form.get('permissionsBSM').patchValue(false);
    //this.form.get('state').patchValue(1);
    this.valueChanged();
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
          this.router.navigateByUrl('/vehicles');
          this.loadingVehicle = false;
        });
      }, (err) => {
        this.toast.error(err);
        this.loadingVehicle = false;
      });
    } else {
      this.vehicle.alias = this.form.get('car').value;
      this.vehicle.guildMemberId = this.form.get('agremiat').value;
      if(this.vehicle.guildId != this.form.get('gremi').value){
        this.vehicle.guildId = this.form.get('gremi').value;
      }
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
        this.vehicle.guildMemberId).subscribe(() => {
          this.translate.get('VEHICLES.UPDATED_CORRECTLY').subscribe((message) => {
            this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
            this.router.navigateByUrl('/vehicles');
            this.loadingVehicle = false;
          });
        }, (err) => {
          this.toast.error(err);
          this.loadingVehicle = false;
        });
    }

    //this.auth.getBearer(),this.gremi.gemiId,this.gremi

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
			this.gremiAnterior == this.form.get('gremi').value &&
			this.estatAnterior == this.form.get('state').value &&
			this.MarcaAnterior == this.form.get('marca').value &&
			this.vehicleAnterior == this.form.get('car').value &&
			this.tipusVehicleAnterior == this.form.get('authorization').value &&
			this.modelAnterior == this.form.get('model').value &&
			this.agremiatAnterior == this.form.get('agremiat').value 
		);
	}


}
