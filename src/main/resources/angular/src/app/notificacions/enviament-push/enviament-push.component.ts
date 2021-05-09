import { Component, OnInit } from '@angular/core';
import { AuthorizationService } from '../../services/authorization.service';
import { NotificationService } from '../../api';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-enviament-push',
  templateUrl: './enviament-push.component.html',
  styleUrls: ['./enviament-push.component.scss']
})
export class EnviamentpushComponent implements OnInit {

  public nots: any;
  public not: any = {
    forAll: true
  };
  public topics: any;
  public destination: String;
  public listDevices = '';
  public sending = false;

  constructor(private auth: AuthorizationService, private notService: NotificationService
    , private _toast: ToastService, private translate: TranslateService) {
    this.notService.notificationsBO(this.auth.getBearer()).subscribe((nots) => {
      this.nots = nots;
      this.not = nots[0];
      this.checkRadios();
    });
    this.notService.topics(this.auth.getBearer()).subscribe((topics) => {
      this.topics = topics;
    });
  }

  sendNotification() {
    this.sending = true;
    if (this.destination === 'all') {
      this.notService.senderByTopic(this.auth.getBearer(),{
        topics: ['i', 'a'],
        notificationId : this.not.notificationId
      }).subscribe((sent) => {
        this.successToast();
      }, error => {
        this.failToast(error);
      });
    } else if (this.destination === 'topic') {
      let topics = [];
      this.topics.forEach(topic => {
        if (topic.checked) {
          topics.push(topic.description);
        }
      });
      this.notService.senderByTopic(this.auth.getBearer(),{
        topics: topics,
        notificationId : this.not.notificationId
      }).subscribe((sent) => {
        this.successToast();
      }, error => {
        this.failToast(error);
      });
    } else if (this.destination === 'devices') {
      let destination = [];
      destination = this.listDevices.split(';');
      this.notService.senderById(this.auth.getBearer(),{
        devices : destination,
        notificationId : this.not.notificationId
      }).subscribe((sent) => {
        this.successToast();
      }, error => {
        this.failToast(error);
      });
    }
  }

  successToast() {
    this.translate.get('PUSH.SENT').subscribe((message) => {
      this._toast.notify('success', message);
      this.sending = false;
    });
  }

  failToast(error) {
    this._toast.error(error);
  }

  isCheckedOneTopic() {
    let isChecked = false;
    if (this.topics) {
      this.topics.forEach(topic => {
        if (topic.checked) {
          isChecked = true;
        }
      });
    }
    return isChecked;
  }

  isChecked(topic) {
    return topic.checked === true;
  }

  toggleTopic(topic) {
    if (this.isChecked(topic)) {
      topic.checked = false;
    } else {
      topic.checked = true;
    }
  }

  checkRadios() {
    if (this.isDisabledAll()) {
      this.destination = 'topic';
    } else {
      this.destination = 'all';
    }
  }

  changeNot($event) {
    this.checkRadios();
  }

  isDisabledAll() {
    return !this.not.forAll;
  }

  isDisabledTopic() {
    return this.not.forAll;
  }

  isDisabledDevices() {
    return this.not.forAll;
  }

  ngOnInit() {
  }

}
