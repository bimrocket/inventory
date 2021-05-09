import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorizationService } from '../../services/authorization.service';
import { UserboService, UserBO } from '../../api';
import { FormControl, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';
import { DataService } from '../../api/api/data.service';
import { WatcherService } from '../../api/api/watcher.service';
import { MatDialog } from '@angular/material';
import { DialogCreateUserBONoLDAPComponent, Remitente } from './dialog-create-user-bono-ldap/dialog-create-user-bono-ldap.component';
import { CurrentUserService } from 'app/services/current-user.service';
import { RolesService } from 'app/services/roles.service';
import { FUNCIONALITIES } from 'app/globalVariables/globalVariables';

@Component({
  selector: 'app-edit-usuaris-bo',
  templateUrl: './edit-usuaris-bo.component.html',
  styleUrls: ['./edit-usuaris-bo.component.scss']
})
export class EditUsuarisBoComponent implements OnInit {

  params: any;
  public user: UserBO = {
    fullName: '',
    email: '',
    profile: {
      profileId: 4
    },
    isLdapUser: true,
    cities: null
  };
  public fullName = new FormControl('', [Validators.required]);
  public email = new FormControl('', [Validators.required, Validators.email]);
  public selectorRol = new FormControl('', [Validators.required]);
  profiles: any = [];
  cities: any = [];
  addingNew: boolean;

  constructor(
    private watcher: WatcherService, private rolesService: RolesService,
    private dialog: MatDialog, private currentUser: CurrentUserService,
    private route: ActivatedRoute, private _authService: AuthorizationService,
    private _usersBoService: UserboService, private translate: TranslateService,
    private _router: Router, private _toast: ToastService, private _dataService: DataService) {
  }


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.params = params;
      this.addingNew = params.id === 'new';
      this._usersBoService.getUserBo(this.params.id, this._authService.getBearer()).subscribe((data) => {
        this.user = data;
        this.email.disable();
        if (!this.user.cities || this.user.cities.length === 0) {
          this.user.cities = '';
        } else {
          this.user.cities = this.user.cities[0];
        }
      });
      this._usersBoService.getAllProfiles().subscribe((profiles) => {
        this.profiles = profiles;
        this.profiles = profiles.filter((profile) => {
          return this.rolesService.isRolAccessible(this.currentUser.getProfile(), profile.description);
        });
        if (this.addingNew) {
          this.user.profile.profileId = this.profiles[0].profileId;
        }
      });
      this._dataService.getCities(!this.currentUser.hasPermission(FUNCIONALITIES.SYSTEM_ADMIN)).subscribe((cities) => {
        this.cities = cities;
        if (!this.isAllMunicipis() && this.addingNew) {
          this.user.cities = this.cities[0].juridicId;
        }
      });
    });
  }

  public isAllMunicipis(): boolean {
    return this.currentUser.canAllMunicipis || this.currentUser.isAdmin();
  }

  saveUser(): void {
    const userRequest: UserBO = {
      fullName: this.user.fullName,
      email: this.user.email,
      isLdapUser: this.user.isLdapUser,
      cities: this.isDisabledOptions() ? [] : [this.user.cities],
      profile: {
        profileId: this.user.profile.profileId
      }
    };
    if (this.addingNew) {
      this._usersBoService.createUserBo(userRequest).subscribe(() => {
        this._router.navigate(['/sistema/usuaris-bo']);
        this.translate.get('EDIT_USUARIS_BO.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (error) => {
        this._toast.error(error);
      });
    } else {
      this._usersBoService.updateUserBo(this.params.id, this._authService.getBearer(), userRequest).subscribe((data) => {
        this._router.navigate(['/sistema/usuaris-bo']);
        this.translate.get('EDIT_USUARIS_BO.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (error) => {
        this._toast.error(error);
      });
    }
  }

  public isDisabledOptions(): boolean {
    return this.user.cities === '';
  }

  toggleLdapUser(): void {
    this.user.isLdapUser = !this.user.isLdapUser;
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
