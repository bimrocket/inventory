import { Injectable } from '@angular/core';
import { CurrentUserService } from './current-user.service';
import { Router, NavigationEnd } from '@angular/router';
import { DUMRoutes, DUM_ROUTES } from '../app.routing';
import { TranslateService } from '@ngx-translate/core';
import { ToastService } from './toast.service';
import { MenuItem, MenuItemChild } from 'app/components/sidebar/sidebar.component';

@Injectable()
export class NavigationEndInterceptor {

    public currentDumRoute: DUMRoutes;
    public currentUrl: string;
    public currentMenuItemParent: MenuItem;
    public currentMenuItemChild: MenuItemChild;

    constructor(
        private translate: TranslateService, private toast: ToastService,
        private curUser: CurrentUserService, private router: Router) {
    }
    

    /**
     * Setups interceptor
     * Should be called in app component on application init
     * @returns void
     */
    public setup(): void {
        this.router.events.subscribe((e) => {
            if (e instanceof NavigationEnd) {
                // Settear currentUrl sense paramQueries
                this.currentUrl = e.url;
                this.currentUrl = this.currentUrl.split('?')[0];
                // Carrega TOKEN JSON i variables de current user
                this.curUser.loadUser();
                // Comprovar si tè permis per accedir a aquesta secció
                this.currentDumRoute = this.getDumRoute(this.currentUrl);
                if (this.currentDumRoute && this.currentDumRoute.permissions) {
                    if (!this.curUser.hasPermission(this.currentDumRoute.permissions)) {
                        // Mostrar error access denegat
                        this.translate.get('GENERAL.ACCESS_DENIED').subscribe((message) => {
                            this.toast.showErrorToast(message);
                        });
                        // Redirigir a dashboard
                        setTimeout(() => {
                            this.router.navigateByUrl('/');
                        }, 2000);
                    }
                }
            }
        });
    }

    private getDumRoute(url: string): DUMRoutes {
        return DUM_ROUTES.find((item: DUMRoutes) => {
            const pathSplitted = item.path.split('/');
            const urlSplitted = url.substring(1, url.length).split('/');
            let isSame = true;
            for (let index = 0; index < pathSplitted.length && isSame; index++) {
                const actualPath = pathSplitted[index];
                const actualUrl = urlSplitted[index];
                isSame = actualUrl && (actualPath === ':id' || actualPath === actualUrl);
            }
            return isSame;
        });
    }
}