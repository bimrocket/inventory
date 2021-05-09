import { Component, OnInit } from '@angular/core';
import { UserService, User } from '../api';
import { AuthorizationService } from '../services/authorization.service';
import { CurrentUserService } from '../services/current-user.service';
import { ToastService } from '../services/toast.service';
import { EstatsUsuari, FUNCIONALITIES } from '../globalVariables/globalVariables';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-gestio-operativa',
  templateUrl: './gestio-operativa.component.html',
  styleUrls: ['./gestio-operativa.component.scss']
})
export class GestioOperativaComponent implements OnInit {

  public search: any = {};
  public users: any = undefined;
  public estatsUsuaris = EstatsUsuari;
  public loading = false;
  public FUNCIONALITIES = FUNCIONALITIES;

  constructor(
    private auth: AuthorizationService, public _currentUserService: CurrentUserService,
    private _userService: UserService, private toast: ToastService, private router: Router) { }

  ngOnInit() {
  }

  getEstatUsuari(user: User) {
    return this.estatsUsuaris.find((item) => {
      return item.value === user.userStateId;
    }).nom;
  }

  searchUser(): void {
    this.loading = true;
    this._userService.users(this.auth.getBearer(), 
    this.search.email, this.search.userStateId, "", this.search.nif)
    .subscribe((users) => {
      this.users = users;
      this.loading = false;
    }, (err) => {
      this.toast.error(err);
      this.loading = false;
    })
  }

  clearParameters(): void {
    this.search = {};
  }

  loadUser(user){
    this._currentUserService.setProject(user.applicationType);
    this.router.navigateByUrl('/gestio-operativa/'+user.userId);
  }

}
