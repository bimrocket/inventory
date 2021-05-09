import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
declare var $: any;

@Injectable()
export class ToastService {

    public TOAST_TYPES = {
        DANGER : 'danger',
        SUCCESS : 'success',
        WARNING : 'warning'
    };
    
    constructor(private translate: TranslateService) { }

    public showErrorToast(message) {
        $.notify({
            icon: 'error',
            message: message
        }, {
            type: 'danger',
            timer: 4000,
            placement: {
                from: 'bottom',
                align: 'right'
            },
            template: '<div data-notify="container" class="col-xl-4 col-lg-4 col-11 col-sm-4 ' +
                ' col-md-4 alert alert-{0} alert-with-icon" role="alert">' +
                '<button mat-button  type="button" aria-hidden="true" class="close mat-button" data-notify="dismiss">' +
                '<i class="material-icons">close</i></button>' +
                '<i class="material-icons" data-notify="icon">error</i> ' +
                '<span data-notify="title">{1}</span> ' +
                '<span data-notify="message">' + message + '</span>' +
                '<div class="progress" data-notify="progressbar">' +
                '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0"' +
                'aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
                '</div>' +
                '<a href="{3}" target="{4}" data-notify="url"></a>' +
                '</div>'
        })
    }

    public error(err) {
        let error = undefined;
        if (err._body) {
            try {
                error = JSON.parse(err._body);
            } catch (e) {
                error = err._body;
            }
        } else if (err.error !== undefined) {
            error = err.error;
            if (err.error instanceof Blob) {
                const fr = new FileReader();
                fr.onload = function() {
                    error = JSON.parse(this.result);
                };
                fr.readAsText(err.error);
            }
        }
        if (err && err.status === 401) {
            return;
        }
        /** We wait the blob to load */
        if (err.error instanceof Blob) {
            const that = this;
            const maxExecutions = 50;
            let i = 0;
            const check = function(){
                i++;
                if (i === maxExecutions) {
                    return;
                }
                if (error.errorCode) {
                    that.showerror(error);
                } else {
                    setTimeout(check, 500); // check again in a second
                }
            }
            check();
        } else {
            /** If not Blob then we show Error */
            this.showerror(error);
        }
    }

    public showerror(error) {
        if (error !== undefined && error !== null && error.errorCode !== null && error.errorCode !== undefined) {

            this.translate.get(error.errorCode).subscribe((res: string) => {
                this.showErrorToast(res);
            });

        } else {

            this.translate.get('error_501').subscribe((res: string) => {
                this.showErrorToast(res);
            });
        }

    }

    public notify(type: string, message: string) {
        $.notify({
            icon: 'notifications',
            message: message
        }, {
                type: type,
                timer: 4000,
                placement: {
                    from: 'bottom',
                    align: 'right'
                },
                template: '<div data-notify="container" class="col-xl-4 col-lg-4 col-11 col-sm-4 ' +
                    ' col-md-4 alert alert-{0} alert-with-icon" role="alert">' +
                    '<button mat-button  type="button" aria-hidden="true" class="close mat-button" data-notify="dismiss">' +
                    '<i class="material-icons">close</i></button>' +
                    '<i class="material-icons" data-notify="icon">notifications</i> ' +
                    '<span data-notify="title">{1}</span> ' +
                    '<span data-notify="message">' + message + '</span>' +
                    '<div class="progress" data-notify="progressbar">' +
                    '<div class="progress-bar progress-bar-{0}" role="progressbar" aria-valuenow="0"' +
                    'aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div>' +
                    '</div>' +
                    '<a href="{3}" target="{4}" data-notify="url"></a>' +
                    '</div>'
        });
    }
}
