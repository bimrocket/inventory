import { Component, OnInit } from '@angular/core';
import { DataService } from '../../api/api/data.service';
import { AuthorizationService } from '../../services/authorization.service';
import { FormControl, Validators } from '@angular/forms';
import { NotificationService } from '../../api';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-add-notificacio',
  templateUrl: './add-notificacio.component.html',
  styleUrls: ['./add-notificacio.component.scss']
})
export class AddNotificacioComponent implements OnInit {

  not: any = {
    notificationType : {
      notificationTypeId : undefined
    },
    forAll : false
  };
  notTypes = [];
  params: any;
  addingNew: any;
  public title_ca = new FormControl('', [Validators.required]);
  public title_es = new FormControl('', [Validators.required]);
  public title_en = new FormControl('', [Validators.required]);
  public message_ca = new FormControl('', [Validators.required]);
  public message_es = new FormControl('', [Validators.required]);
  public message_en = new FormControl('', [Validators.required]);
  constructor(private route: ActivatedRoute, private auth: AuthorizationService, 
    private _dataService: DataService, private _notService: NotificationService,
  private _router: Router, private translate: TranslateService, private _toast: ToastService) {
    this.route.params.subscribe(params => {
      this.params = params;
      this.addingNew = params.id === 'new';
    });
    this.loadNotification();
    this._dataService.notificationTypes(auth.getBearer()).toPromise().then(notTypes => {
      this.notTypes = notTypes;
      if (this.not.notificationType.notificationTypeId === undefined) {
        this.not.notificationType.notificationTypeId = notTypes[0].notificationTypeId;
      }
    });
  }

  ngOnInit() {
  }
  loadNotification() {
    if (!this.addingNew) {
      this._notService.getNotification(this.params.id,this.auth.getBearer()).subscribe(not => {
        this.not = not;
      }, (error) => {
        this._toast.error(error);
      });
    }
  }

  saveNotification() {
    if (this.addingNew) {
      this._notService.addNotification(this.auth.getBearer(), this.not).subscribe((data) => {
        this._router.navigate(['/notificacions/llista']);
        this.translate.get('ADD_NOTIFICACIONS.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (error) => {
        this._toast.error(error);
      });
    } else {
      this._notService.updateNotification(this.params.id, this.auth.getBearer(), this.not).subscribe((data) => {
        this._router.navigate(['/notificacions/llista']);
        this.translate.get('ADD_NOTIFICACIONS.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (error) => {
        this._toast.error(error);
      });
    }

  }

}
