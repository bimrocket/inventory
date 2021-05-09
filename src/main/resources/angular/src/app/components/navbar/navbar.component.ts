import { Component, OnInit, ElementRef } from '@angular/core';
import { Location, LocationStrategy, PathLocationStrategy } from '@angular/common';
import { Router } from '@angular/router';
import { CurrentUserService } from '../../services/current-user.service';
import { DataService } from '../../api/api/data.service';
import { AuthorizationService } from '../../services/authorization.service';
import { JuridicEntity } from '../../api';
import { NavigationEndInterceptor } from 'app/services/navigationEndInterceptor.service';
import { APLICATION } from '../../globalVariables/globalVariables';
@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

    public readonly listProjects = APLICATION;

    public mobile_menu_visible: any = 0;
    private toggleButton: any;
    private sidebarVisible: boolean;
    public municipis: JuridicEntity[];
    public currentMunicipi: number;

    constructor(
        private navigation: NavigationEndInterceptor,
        public auth: AuthorizationService,
        public data: DataService,
        public _currentUserService: CurrentUserService,
        public location: Location,
        private element: ElementRef, private router: Router) {
    }

    public ngOnInit(): void {
        this.sidebarVisible = false;
        this.data.getCities(true).subscribe((cities) => {
            this.municipis = cities;
            this.currentMunicipi = this._currentUserService.getMunicipi();
            if (!this.currentMunicipi) {
                this.currentMunicipi = this.municipis[0].juridicId;
                this._currentUserService.setMunicipi(this.currentMunicipi);
            } else {
                let foundMun = false;
                for (let index = 0; index < this.municipis.length && !foundMun; index++) {
                    const element = this.municipis[index];
                    if (element.juridicId === this.currentMunicipi) {
                        foundMun = true;
                    }
                }
                if (!foundMun) {
                    this.currentMunicipi = this.municipis[0].juridicId;
                    this._currentUserService.setMunicipi(this.currentMunicipi);
                }
            }
            const navbar: HTMLElement = this.element.nativeElement;
            this.toggleButton = navbar.getElementsByClassName('navbar-toggler')[0];
            this.router.events.subscribe((event) => {
                this.sidebarClose();
                const $layer: any = document.getElementsByClassName('close-layer')[0];
                if ($layer) {
                    $layer.remove();
                    this.mobile_menu_visible = 0;
                }
            });
        });
    }


    /**
     * Comparator for municipi selector
     * @param  {number} munId1
     * @param  {number} munId2
     * @returns boolean true or false if same municipi
     */
    public munComparator(munId1: number, munId2: number): boolean {
        return munId1 === munId2;
    }

    public muncipiChanged($event) {
        this._currentUserService.setMunicipi($event.value);
        window.location.reload();
    }

    public getTitle(): string {
        if(typeof this.navigation.currentMenuItemParent !== "undefined"){
            return this.navigation.currentMenuItemParent.title;
        }else{
            return '';
        }
    }

    public hasChildMenu(): boolean {
        return this.navigation.currentMenuItemChild !== undefined;
    }

    public getTitleChild(): string {
        return this.hasChildMenu() ? this.navigation.currentMenuItemChild.title : '';
    }


    sidebarOpen() {
        const toggleButton = this.toggleButton;
        const body = document.getElementsByTagName('body')[0];
        setTimeout(function () {
            toggleButton.classList.add('toggled');
        }, 500);

        body.classList.add('nav-open');

        this.sidebarVisible = true;
    };
    sidebarClose() {
        const body = document.getElementsByTagName('body')[0];
        this.toggleButton.classList.remove('toggled');
        this.sidebarVisible = false;
        body.classList.remove('nav-open');
    };
    sidebarToggle() {
        // const toggleButton = this.toggleButton;
        // const body = document.getElementsByTagName('body')[0];
        var $toggle = document.getElementsByClassName('navbar-toggler')[0];

        if (this.sidebarVisible === false) {
            this.sidebarOpen();
        } else {
            this.sidebarClose();
        }
        const body = document.getElementsByTagName('body')[0];

        if (this.mobile_menu_visible == 1) {
            // $('html').removeClass('nav-open');
            body.classList.remove('nav-open');
            if ($layer) {
                $layer.remove();
            }
            setTimeout(function () {
                $toggle.classList.remove('toggled');
            }, 400);

            this.mobile_menu_visible = 0;
        } else {
            setTimeout(function () {
                $toggle.classList.add('toggled');
            }, 430);

            var $layer = document.createElement('div');
            $layer.setAttribute('class', 'close-layer');


            if (body.querySelectorAll('.main-panel')) {
                document.getElementsByClassName('main-panel')[0].appendChild($layer);
            } else if (body.classList.contains('off-canvas-sidebar')) {
                document.getElementsByClassName('wrapper-full-page')[0].appendChild($layer);
            }

            setTimeout(function () {
                $layer.classList.add('visible');
            }, 100);

            $layer.onclick = function () { //asign a function
                body.classList.remove('nav-open');
                this.mobile_menu_visible = 0;
                $layer.classList.remove('visible');
                setTimeout(function () {
                    $layer.remove();
                    $toggle.classList.remove('toggled');
                }, 400);
            }.bind(this);

            body.classList.add('nav-open');
            this.mobile_menu_visible = 1;

        }
    };

    public logout(): void {
       this.router.navigateByUrl('/login');
        this.auth.logout();
    }

    
    public projectChanged($event) {
        this._currentUserService.setProject($event.value);
        this.router.navigateByUrl('/inici');
        //window.location.reload();
    }
}
