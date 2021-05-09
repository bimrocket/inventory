import { Injectable } from '@angular/core';
import { TicketsService, User, JuridicEntity } from '../api';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from './toast.service';
import { AuthorizationService } from './authorization.service';
import * as moment from 'moment';

export interface MonthlyExtract {
  month: number,
  year: number,
  name: String
};

@Injectable({
  providedIn: 'root'
})
export class ExtractesService {

  public downloadInProgress = false;
  public monthlyExtracts: Array<MonthlyExtract> = [];

  constructor(
    private translate: TranslateService, private auth: AuthorizationService,
    private tickets: TicketsService, private toast: ToastService) { }

  public init() {
    this.monthlyExtracts = [];
    for (let i = 0; i < 12; i++) {
      const newToday = moment().locale('ca-ES');
      newToday.startOf('month');
      newToday.set('month', newToday.month() - i);
      const monthlyExtract = {
        month: newToday.month() + 1,
        name: newToday.format('MMMM'),
        year: newToday.year()
      };
      this.monthlyExtracts.push(monthlyExtract);
    }
  }
  public getPdfMonthlyExtract(monthlyExtract: MonthlyExtract, user: User) {
    this.dlInProgress();
    this.tickets.monthlyExtract(monthlyExtract.year.toString(), monthlyExtract.month,
      this.auth.getBearer(), user.accountId).subscribe((pdf) => {
        saveAs(pdf, 'Extracte ' + monthlyExtract.month + '/' + monthlyExtract.year + '.pdf');
        this.downloadInProgress = false;
      }, (err) => {
        this.downloadInProgress = false;
        this.toast.error(err);
      });
  }

  public getCityPdfMonthlyExtract(monthlyExtract: MonthlyExtract, city: JuridicEntity, user?: User) {
    this.dlInProgress();
    this.tickets.monthlyExtractByCity(city.juridicId, monthlyExtract.year, monthlyExtract.month, this.auth.getBearer()).subscribe((pdf) => {
      saveAs(pdf, 'Extracte ' + city.name + ' ' + monthlyExtract.month + '/' + monthlyExtract.year + '.pdf');
      this.downloadInProgress = false;
    }, (err) => {
      this.downloadInProgress = false;
      this.toast.error(err);
    });
  }

  private dlInProgress() {
    this.downloadInProgress = true;
    this.translate.get('CERCADOR.DOWNLOAD_IN_PROGRESS').subscribe((message) => {
      this.toast.notify('warning', message);
    });
  }

}
