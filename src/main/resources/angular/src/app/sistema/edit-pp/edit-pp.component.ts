import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorizationService } from '../../services/authorization.service';
import { TermsService } from '../../api/api/terms.service';
import { FormControl, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';
import { PrivacyPolicy } from '../../api/model/privacyPolicy';

@Component({
  selector: 'app-edit-pp',
  templateUrl: './edit-pp.component.html',
  styleUrls: ['./edit-pp.component.scss']
})
export class EditPpComponent implements OnInit {

  public params;
  public addingNew;
  public pp: PrivacyPolicy = {
    url_ca: '',
    url_es: '',
    url_en: '',
    url: '',
    active: 0,
    type: {
      privacyPolicyType: 1,
      description : 'null',
      level : 0
    },
    privacyPoliciesId : 0,
    publishDate : new Date().toISOString(),
    client: 'dum',
    version : 0
  };
  public pps = [
    {
      privacyPolicyType : 1,
      description : 'basic',
      level : 1
    },
    {
      privacyPolicyType : 3,
      description : 'plus plus',
      level : 3
    }
  ];
  public url_ca = new FormControl('', [Validators.required]);
  public url_es = new FormControl('', [Validators.required]);
  public url_en = new FormControl('', [Validators.required]);
  public version = new FormControl('', [Validators.required]);

  constructor(private _router: Router, private translate: TranslateService, private _toast: ToastService,
    private route: ActivatedRoute, private auth: AuthorizationService, private terms: TermsService) {
    this.route.params.subscribe(params => {
      this.params = params;
      this.addingNew = params.id === undefined;
      if (!this.addingNew) {
        this.terms.getPrivacyPolicyById(params.id).subscribe((pp) => {
          this.pp = pp;
        }, (err) => {
          this._toast.error(err);
        });
      }
    });
  }

  saveTerm() {
    if (this.addingNew) {

      this.terms.createPrivacyPolicy(this.pp).subscribe((ok) => {
        this._router.navigate(['/sistema/pp']);
        this.translate.get('PP.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (err) => {
        this._toast.error(err);
      })

    } else {

      this.terms.editPrivacyPolicy(this.pp.privacyPoliciesId, this.pp).subscribe((ok) => {
        this._router.navigate(['/sistema/pp']);
        this.translate.get('PP.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (err) => {
        this._toast.error(err);
      })

    }

  }

  toggleActive() {
    if (this.pp.active) {
      this.pp.active = 0;
    } else {
      this.pp.active = 1;
    }
  }

  ngOnInit() {
  }

}
