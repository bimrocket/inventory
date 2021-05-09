import { DatePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { format } from 'util';

@Pipe({
    name: 'localizedDate',
    pure: false
})
export class LocalizedDatePipe implements PipeTransform {

    constructor(private translateService: TranslateService) {
    }

    transform(value: any): any {
        let date;
        if (typeof(value) === 'string') {
            date = this.parseISOString(value);
        } else if (value === null || value === undefined) {
            return '-';
        } else {
            date = new Date(value);
        }
        /*
        const options = {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        };

        return date.toLocaleString(this.translateService.currentLang, options);
        */
        let formatted = '';
        formatted += (this.addZero(date.getDate()) + '/');
        formatted += (this.addZero((date.getMonth()) + 1) + '/');
        formatted += (this.addZero(date.getFullYear()) + ' ');
        formatted += (this.addZero(date.getHours()) + ':');
        formatted += (this.addZero(date.getMinutes()) + ':');
        formatted += (this.addZero(date.getSeconds()) + '');

        return formatted;
    }

    addZero(value) {
        if (value < 10) {
            value = '0' + value;
        }
        return value;
    }

    parseISOString(s) {
        const b = s.split(/\D+/);
        return new Date(Date.UTC(b[0], --b[1], b[2], b[3], b[4], b[5], b[6]));
    }

}
