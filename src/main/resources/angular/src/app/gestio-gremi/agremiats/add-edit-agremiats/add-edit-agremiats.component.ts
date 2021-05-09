import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, FormBuilder } from '@angular/forms';
import { Gremi } from '../../../api/model/gremi';
import { Agremiat } from '../../../api/model/agremiat';
import { Router, ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from '../../../services/toast.service';
import { DataService } from '../../../api/api/data.service';
import { GuildMember } from '../../../api/model/guildMember';
import { JuridicEntity, GuildMemberService, GuildService } from '../../../api';
import { Guild } from '../../../api/model/guild';
import { AuthorizationService } from '../../../services/authorization.service';
import { GuildMemberType } from '../../../api/model/guildMemberType';
import { config } from 'config/config';
import { FileService } from '../../../services/file.service';
import { documentValidator } from 'app/validators/autEmpValidate';

@Component({
  selector: 'app-add-edit-agremiats',
  templateUrl: './add-edit-agremiats.component.html',
  styleUrls: ['./add-edit-agremiats.component.scss']
})
export class AddEditAgremiatsComponent implements OnInit {

  params: any;
  addingNew:Boolean;
  public error: any = {};
  public agremiat:GuildMember = {} as any;
  public agremiatTypeCod = 0;
  public municipis: JuridicEntity[];
  public guilds: Guild[];
  public types: GuildMemberType[];
  public translationParameters = null;

  public form = this._formBuilder.group({    
    nameCompany: ['', [Validators.required]],
    fullName :  ['', [Validators.required]],
    dni: ['', [Validators.required, documentValidator('agremiatType')]],
    email:  ['', [Validators.required]],
    phone:  ['', [Validators.required]],
    agremiatType :  ['', [Validators.required]],
    securityCode :  ['', [Validators.required]],
    zipCode : ['', [Validators.required]],
    cityId : ['', [Validators.required]],
    address :  ['', [Validators.required]],
    guildId :  ['', [Validators.required]]
  });

  constructor(
    private toast: ToastService, private gmService: GuildMemberService,private _formBuilder: FormBuilder,
    private guildService: GuildService, private auth: AuthorizationService,
    private router: Router, private translate: TranslateService,private data: DataService,private route: ActivatedRoute,private fileService: FileService
  ) { }

  ngOnInit() {

    this.guildService.guildGet(this.auth.getBearer()).subscribe((guilds) => {
      this.guilds = guilds;
    })
    this.gmService.guildMemberType(config.LOCALE, this.auth.getBearer()).subscribe((types) => {
      this.types = types;
    });
    this.data.getCities(true).subscribe((cities) => {
      this.municipis = cities.filter(citi => citi.nif != 'P1111111Z');
      
    });
    this.route.params.subscribe(params => {
      this.params = params;
      if(this.params.id == "new") {
        this.addingNew = true;
        this.form.get('securityCode').disable();
        this.form.get('securityCode').patchValue('AUTOGENERAT');
        this.agremiatTypeCod = 0;
        this.form.get('agremiatType').patchValue('0');
      }else{
        this.addingNew = false;
        this.getMember();
      }
    });

    
  }

  getMember(){
    this.gmService.guildMemberDetailed(this.params.id, this.auth.getBearer()).subscribe((agremiat) => {
      this.agremiat = agremiat;
      this.form.get('nameCompany').patchValue(this.agremiat.companyName);
      this.form.get('fullName').patchValue(this.agremiat.memberName);
      this.form.get('dni').patchValue(this.agremiat.dni);
      this.form.get('email').patchValue(this.agremiat.email);
      this.form.get('phone').patchValue(this.agremiat.telephone);
      this.form.get('agremiatType').patchValue(this.agremiat.agremiatType);
      this.form.get('securityCode').patchValue(this.agremiat.securityCode);
      this.form.get('zipCode').patchValue(this.agremiat.zipCode);
      this.form.get('cityId').patchValue(this.agremiat.cityId);
      this.form.get('address').patchValue(this.agremiat.address);
      this.form.get('guildId').patchValue(this.agremiat.guildId);
      this.agremiatTypeCod = this.agremiat.agremiatType;

    }, (err) => {
      this.toast.error(err);
    });
  }

  save() {
    this.agremiat.memberName = this.form.get('fullName').value;
    this.agremiat.companyName = this.form.get('nameCompany').value;
    this.agremiat.dni = this.form.get('dni').value;
    this.agremiat.email = this.form.get('email').value;
    this.agremiat.telephone = this.form.get('phone').value;
    this.agremiat.agremiatType = this.form.get('agremiatType').value;
    if (!this.addingNew) {
      this.agremiat.securityCode = this.form.get('securityCode').value;
    }
    this.agremiat.zipCode = this.form.get('zipCode').value;
    this.agremiat.cityId = this.form.get('cityId').value;
    this.agremiat.address = this.form.get('address').value;
    this.agremiat.guildId = this.form.get('guildId').value;

    if (this.addingNew) {
      this.gmService.createGuildMember(this.auth.getBearer(), this.agremiat).subscribe(() => {
        this.translate.get('AGREMIATS.CREATED', {
          nom : this.agremiat.companyName
        }).subscribe((message) => {
          this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
          this.router.navigateByUrl('/gestio-gremi/agremiats');
        });
      }, (err) => {
        this.toast.error(err);
      });
    } else {
      this.gmService.updateGuildMember(this.agremiat.memberId, this.auth.getBearer(), this.agremiat).subscribe(() => {
        this.translate.get('AGREMIATS.UPDATED', {
          nom : this.agremiat.companyName
        }).subscribe((message) => {
          this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
          this.router.navigateByUrl('/gestio-gremi/agremiats');
        });
      }, (err) => {
        this.toast.error(err);
      })
    }
  }

  download(element){
    this.fileService.seeFile(element.fileDocument, element.fileType, element.fileName);
  }

  public changeType(){
    var idAgremiatType = this.form.get('agremiatType');
    this.agremiatTypeCod  = idAgremiatType.value
  }


}
