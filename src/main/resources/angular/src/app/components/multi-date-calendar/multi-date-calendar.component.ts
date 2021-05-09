import { Component, OnInit, HostBinding, Input, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';

@Component({
  selector: 'app-multi-date-calendar',
  templateUrl: './multi-date-calendar.component.html',
  styleUrls: ['./multi-date-calendar.component.scss']
})
export class MultiDateCalendarComponent implements OnInit {

  @Input('currentMonth')
  private currentMonth;

  @Input('selectedDays')
  private selectedDays = [];

  @Input('disabledBefore')
  private disabledBefore;

  @Input('disabledAfter')
  private disabledAfter;

  public weekDays;
  public rows;
  private lastRowEmpty = false;

  @Output()
  private selectedDaysChange = new EventEmitter();

  constructor() { }

  ngOnInit() {
    this.init();
    this.generate();
  }

  init() {
    let today = moment().locale('ca-ES').startOf('week');
    this.weekDays = [
      today.format('ddd'),
      today.add(1, 'days').format('ddd'),
      today.add(1, 'days').format('ddd'),
      today.add(1, 'days').format('ddd'),
      today.add(1, 'days').format('ddd'),
      today.add(1, 'days').format('ddd'),
      today.add(1, 'days').format('ddd')
    ];
  }

  generate() {
    this.rows = [];
    this.lastRowEmpty = false;
    const clonedCurrentMonth = this.currentMonth.clone();
    let momentDay = clonedCurrentMonth.startOf('month');
    let startingIndex = momentDay.weekday();
    let stop = false;
    for (let i = 0; i < 6; i++) {
      this.rows[i] = [];
      for (let j = 0; j < 7; j++) {
        if (j >= startingIndex) {
          if (stop) {
            this.rows[i][j] = null;
          } else {
            this.rows[i][j] = momentDay.clone();
            momentDay = momentDay.clone().add(1, 'milliseconds').add(1, 'days');
            if (momentDay.clone().add(1, 'milliseconds').isSameOrAfter(clonedCurrentMonth.endOf('month'))) {
              stop = true;
            }
          }
        } else {
          this.rows[i][j] = null;
        }
      }
      startingIndex = 0;
    }
    if (this.rows[5][0] === null) {
      this.lastRowEmpty = true;
    }
  }

  isSelected(day) {
    if (day === null) {
      return false;
    }
    let selected = false;
    this.selectedDays.forEach((momentDay) => {
      if (day.isSame(momentDay, 'day')) {
        selected = true;
      }
    });
    return selected;
  }

  toggleSelection(day) {
    if (this.isDisabled(day)) {
      return false;
    }
    if (this.isSelected(day)) {
      this.selectedDays = this.selectedDays.filter((elem) => {
        return !(elem.isSame(day, 'day'));
      });
      this.selectedDaysChange.emit(this.selectedDays);
    } else {
      this.selectedDays.push(day.hour(1));
      this.selectedDaysChange.emit(this.selectedDays);
    }
  }

  isDisabledLeft() {
    const leftMonth = this.currentMonth.clone().endOf('month').subtract(1, 'months');
    return (this.disabledBefore && this.disabledBefore.isSameOrAfter(leftMonth));
  }

  isDisabledRight() {
    const rightMonth = this.currentMonth.clone().startOf('month').add(1, 'months');
    return (this.disabledAfter && this.disabledAfter.isSameOrBefore(rightMonth));
  }

  isDisabled(day) {
    if (day === null) {
      return false;
    }
    let disabled = false;
    if (this.disabledBefore) {
      disabled = day.isBefore(this.disabledBefore);
    }

    if (!disabled && this.disabledAfter) {
      disabled = day.isAfter(this.disabledAfter);
    }
    return disabled;
  }

  goLeft() {
    this.currentMonth.subtract(1, 'months');
    this.generate();
  }

  goRight() {
    this.currentMonth.add(1, 'months');
    this.generate();
  }

  getMonth() {
    return this.currentMonth.format('MMMM') + ' ' + this.currentMonth.year();
  }


}
