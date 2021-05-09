import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorizationService } from '../../../services/authorization.service';
import { UserboService, JuridicEntity } from '../../../api';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../services/toast.service';
import { DataService } from '../../../api/api/data.service';
import { WatcherService } from '../../../api/api/watcher.service';
import { Watcher } from '../../../api/model/watcher';

@Component({
  selector: 'app-add-edit-vigilants',
  templateUrl: './add-edit-vigilants.component.html',
  styleUrls: ['./add-edit-vigilants.component.scss']
})
export class AddEditVigilantsComponent implements OnInit {

  params: any;
  watch: Watcher = {
    watcherId : -1,
    fullName : '',
    email : '',
    city : {
      juridicId : 1,
      nif : '',
      name : ''
    }
  };
  public fullName = new FormControl('', [Validators.required]);
  public email = new FormControl('', [Validators.required, Validators.email]);
  profiles: any = [];
  cities: any = [];
  addingNew: boolean;

  constructor(
    private watcher: WatcherService,
    private route: ActivatedRoute, private translate: TranslateService,
    private router: Router, private toast: ToastService, private _dataService: DataService) {
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.params = params;
      this.addingNew = params.id === undefined;
      if (!this.addingNew) {
        this.watcher.watcher(this.params.id).subscribe((watch) => {
          this.watch = watch;
        });
      }
      this._dataService.getCities(true).subscribe((cities) => {
        this.cities = cities;
        if (this.addingNew) {
          this.watch.city = cities[0];
        }
      });
    });
  }

  cityComparator(a1: JuridicEntity, a2: JuridicEntity) {
    return a1.juridicId === a2.juridicId;
  }

  saveUser(): void {
    if (this.addingNew) {
      this.watcher.createWatcher(this.watch).subscribe((ok) => {
        this.translate.get('VIGILANTS.CREATED').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/vigilants/gestio']);
        })
      }, (err) => {
        this.toast.error(err);
      })
    } else {
      this.watcher.editWatcher(this.watch.watcherId, this.watch).subscribe((ok) => {
        this.translate.get('VIGILANTS.SAVED').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/vigilants/gestio']);
        })
      }, (err) => {
        this.toast.error(err);
      })
    }
  }

  getEmailErrorMessage() {
    return this.email.hasError('required') ? 'GENERAL.YOU_NEED_VALUE' :
      this.email.hasError('email') ? 'GENERAL.INVALID_EMAIL' :
        '';
  }

  getFullNameErrorMessage() {
    return this.fullName.hasError('required') ? 'GENERAL.YOU_NEED_VALUE' :
      '';
  }

}
