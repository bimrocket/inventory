import { Component, OnInit } from '@angular/core';
import { ToastService } from '../../../services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, Validators, FormGroup, FormBuilder } from '@angular/forms';
import { GremisService } from '../../../api/api/gremis.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { TranslateService } from '@ngx-translate/core';
import { DataService } from '../../../api/api/data.service';
import { GuildService } from '../../../api/api/guild.service';
import { Guild } from 'app/api/model/guild';

@Component({
  selector: 'app-add-edit-gremi',
  templateUrl: './add-edit-gremi.component.html',
  styleUrls: ['./add-edit-gremi.component.scss']
})
export class AddEditGremiComponent implements OnInit {

  municipis;
  params: any;
  guild: Guild = {} as any;
  loadingGuild = true;
  exist = false;
  error: any = {
    errorCode: null
  }

  protected cuentaForm = this._formBuilder.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    owerName: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.maxLength(200), Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$')]],
    gremiDesc: ['', [Validators.required, Validators.maxLength(200)]],
    nif: ['', [Validators.required, Validators.maxLength(15)]],
    address: ['', [Validators.required, Validators.maxLength(100)]],
    zipCode: ['', [Validators.required, Validators.maxLength(22)]],
    cityId: ['', [Validators.required]],
    telefoneNumber: ['', [Validators.required, Validators.maxLength(9)]],
    dumGranted: [false],
    parkingBsmGranted: [false],
    expirationDate: ['', [Validators.required]]
  });

  constructor(
    private router: Router,
    private auth: AuthorizationService, private route: ActivatedRoute, private _gremisService: GremisService, private toast: ToastService,
    private translate: TranslateService, private _guildService: GuildService, private data: DataService, private _formBuilder: FormBuilder,
    private _route: Router) {

  }

  ngOnInit() {

    this.data.getCities(true).subscribe((cities) => {
      this.municipis = cities.filter(citi => citi.nif != 'P1111111Z');
      this.route.params.subscribe(params => {
        this.params = params;
        if (this.params.id != "new") {
          this.getUser();
        }else{
          this.loadingGuild = false;
        }
      });
    },
      (err) => { this.loadingGuild = false; this.showInitError(err); })

  }

  private getUser() {
    this._guildService.guildGuildIdDetailedGet(
      this.params.id, this.auth.getBearer()).subscribe(
        (guild) => {
          this.loadingGuild = false;
          this.exist = true;
          this.guild = guild;
        },
        (err) => { this.loadingGuild = false; this.showInitError(err); }
      );


  }


  save() {
    if (this.cuentaForm.status == 'INVALID') {
      this.translate.get('error_167').subscribe((message) => {
        this.error.errorCode = message;
        this.toast.showerror(this.error);
      });
      this.error.errorCode = null;
      return;
    }

    if (this.cuentaForm.value['dumGranted'] == null) {
      this.guild.dumGranted = false;
    }

    if (this.cuentaForm.value['parkingBsmGranted'] == null) {
      this.guild.parkingBsmGranted = false;
    }
    if (this.params.id == "new") {
      this.guild.gremiId = 0;
      this.guild.state = 1;
      this._guildService.guildPost(this.auth.getBearer(), this.guild).subscribe(
        (guild) => {
          this.guild = guild;
          this.translate.get('GREMIS.CREATED', { guild : this.guild.name }).subscribe((message) => {
            this.toast.notify('success', message);
          });
          this.router.navigateByUrl('/gestio-gremi/gremis');
        },
        (err) => {
          this.toast.error(err);
        });
    } else {
      this._guildService.guildGuildIdPut(
        this.params.id, this.auth.getBearer(), this.guild).subscribe(
          (guild) => {
            this.translate.get('GREMIS.UPDATED', { guild : this.guild.name }).subscribe((message) => {
              this.toast.notify('success', message);
            });
            this.router.navigateByUrl('/gestio-gremi/gremis');
          },
          (err) => { this.toast.error(err); }
        );
    }
  }

  private showInitError(err: any) {
    if (err.status !== 401) {
      if (err.error && err.error.errorCode) {
        this.translate.get('err.error.errorCode').subscribe((message) => {
          this.toast.notify('danger', message);
        });
      } else {
        this.translate.get('GREMI.INIT_ERROR_TITLE').subscribe((message) => {
          this.toast.notify('danger', message);
        });
      }
    }
  }

}
