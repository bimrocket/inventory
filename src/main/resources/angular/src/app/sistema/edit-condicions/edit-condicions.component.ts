import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthorizationService } from '../../services/authorization.service';
import { TermsService } from '../../api/api/terms.service';
import { FormControl, Validators } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../services/toast.service';
import { ServiceCondition } from '../../api/model/serviceCondition';

@Component({
  selector: 'app-edit-condicions',
  templateUrl: './edit-condicions.component.html',
  styleUrls: ['./edit-condicions.component.scss']
})
export class EditCondicionsComponent implements OnInit {

  public params;
  public addingNew;
  public term: ServiceCondition = {
    serviceConditionId : 0,
    url_ca : '',
    url_es : '',
    url_en : '',
    url : '',
    active : 0,
    client: 'dum',
    version: 0,
    publishDate: new Date().toISOString()
  };
  public url_ca = new FormControl('', [Validators.required]);
  public url_es = new FormControl('', [Validators.required]);
  public url_en = new FormControl('', [Validators.required]);
  public version = new FormControl('', [Validators.required]);

  constructor(
    private toast: ToastService,
    private _router: Router, private translate: TranslateService, private _toast: ToastService,
    private route: ActivatedRoute, private auth: AuthorizationService, private terms: TermsService) {
    this.route.params.subscribe(params => {
      this.params = params;
      this.addingNew = params.id === undefined;
      if (!this.addingNew) {
        this.terms.getServiceConditionById(params.id).subscribe((term: ServiceCondition) => {
          this.term = term;
        }, (err) => {
          this.toast.error(err);
        });
      }
    });
  }

  saveTerm() {
    if (!this.addingNew) {
      this.terms.editServiceCondition(this.term.serviceConditionId, this.term).subscribe((ok) => {
        this._router.navigate(['/sistema/condicions']);
        this.translate.get('TERMS.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (err) => {
        this.toast.error(err);
      })

    } else {
      this.terms.createServiceCondition(this.auth.getBearer(), this.term).subscribe((ok) => {
        this._router.navigate(['/sistema/condicions']);
        this.translate.get('TERMS.SAVED_CORRECTLY').subscribe((message) => {
          this._toast.notify('success', message);
        });
      }, (err) => {
        this.toast.error(err);
      })

    }

  }

  toggleActive() {
    if (this.term.active) {
      this.term.active = 0;
    } else {
      this.term.active = 1;
    }
  }

  ngOnInit() {
  }

}
