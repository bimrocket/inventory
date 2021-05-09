import { Component, OnInit } from '@angular/core';
import { DataService } from '../api/api/data.service';
import { AuthorizationService } from '../services/authorization.service';
import { MatDialog } from '@angular/material';
import { RefundTicketDialogComponent } from '../gestio-operativa/edit-users/refund-ticket-dialog/refund-ticket-dialog.component';
import { TicketsService, ParkService, ZoneType } from '../api';
import { FormControl, Validators } from '@angular/forms';
import { DIalogStopTicketComponent } from '../gestio-operativa/edit-users/dialog-stop-ticket/dialog-stop-ticket.component';
import { ToastService } from '../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { CercadorOperacionsService } from '../services/cercador-operacions.service';
import {DateAdapter} from '@angular/material';
import { CurrentUserService } from '../services/current-user.service';
import { APLICATION } from '../globalVariables/globalVariables';

@Component({
  selector: 'app-cercador-operacions',
  templateUrl: './cercador-operacions.component.html',
  styleUrls: ['./cercador-operacions.component.scss']
})
export class CercadorOperacionsComponent implements OnInit {

  public cercadorMat = new FormControl('', [Validators.required]);
  public cercadorTiquets = new FormControl('', [Validators.required]);
  public selectorCities = new FormControl('', [Validators.required]);
  public ticketTypes;
  public zoneTypes;
  public cities;
  public changeRangDate = false;
  public textAnt = null;
  public isDum = false;
  constructor(
    public cercador: CercadorOperacionsService,  private translate: TranslateService,private dateAdapter: DateAdapter<any>,
    public _currentUserService: CurrentUserService,
  ) {
  }


  ngOnInit() {
    this.cercador.init();
    this.dateAdapter.getFirstDayOfWeek = () => { return 1; }
    this.isDum = this._currentUserService.getProject() == APLICATION.ZONA_DUM
  }

  antiguetatChanged(event){
    this.cercador.antiguetatChanged(event)
  }
  
  changeDate(){
    this.cercador.changeDate(); 
  }

  changePlateNumber(){
    this.cercador.changePlateNumber(); 
  }

}
