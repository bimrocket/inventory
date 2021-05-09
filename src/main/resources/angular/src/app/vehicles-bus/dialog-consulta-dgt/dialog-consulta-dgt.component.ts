import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { VehicleBusService } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { VehicleDGTRes } from '../../api/model/vehicleDGTRes';
import { ToastService } from '../../services/toast.service';
import { VehicleDocumentation } from '../../api/model/vehicleDocumentation';
import { FileService } from '../../services/file.service';


@Component({
  selector: 'app-dialog-consulta-dgt',
  templateUrl: './dialog-consulta-dgt.component.html',
  styleUrls: ['./dialog-consulta-dgt.component.scss']
})
export class DialogConsultaDGTBusComponent implements OnInit {

  public dgt: VehicleDGTRes;
  public loading = false;
  public vehicleDocumentation: VehicleDocumentation[];
  public dni = "-";

  constructor(
    private toast: ToastService,
    private vehicleService: VehicleBusService, private auth: AuthorizationService, private fileService: FileService, 
    private thisDialogRef: MatDialogRef<DialogConsultaDGTBusComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.loading = true;
    this.vehicleService.vehicleDgt(this.data.plateNumber, this.auth.getBearer()).subscribe((dgt) => {
      this.dgt = dgt;
      this.loading = false;
    }, (err) => {
      this.toast.error(err);
      this.loading = false;
    });
  }

  public isEmpty(): boolean {
    if (!this.loading) {
      return this.dgt.codiIndustria === ''
        && this.dgt.ecoTag === null
        && this.dgt.marca === null
        && this.dgt.modelo === null
        && this.dgt.tipoVehiculoDGT === null
        && this.dgt.tipoVehiculoDUM === null;
    } else {
      return false;
    }
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }

  public getField(campoDGT: string): string {
    if (campoDGT !== null && campoDGT !== '') {
      return campoDGT;
    } else {
      return 'DIALOG_CONSULTA_DGT.NO_DATA_FIELD';
    }
  }

  download(id) {
    this.vehicleDocumentation.forEach(element => {
      if (element.docId == id) {
        this.fileService.seeFile(element.fileDocument, element.fileType, element.fileName);
      }
    });
  }


}
