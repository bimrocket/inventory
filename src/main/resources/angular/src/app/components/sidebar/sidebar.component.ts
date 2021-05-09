import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CurrentUserService } from '../../services/current-user.service';
import { environment } from '../../../environments/environment';
import { AuthorizationService } from '../../services/authorization.service';
import { config } from 'config/config';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';
import { NavigationEndInterceptor } from 'app/services/navigationEndInterceptor.service';
import { DashboardComponent } from 'app/dashboard/dashboard.component';
import { GestioOperativaComponent } from 'app/gestio-operativa/gestio-operativa.component';
import { EditUsersComponent } from 'app/gestio-operativa/edit-users/edit-users.component';
import { CercadorOperacionsComponent } from 'app/cercador-operacions/cercador-operacions.component';
import { AreesConfiguracionsComponent } from 'app/arees/arees-configuracions/arees-configuracions.component';
import { TramsComponent } from 'app/arees/trams/trams.component';
import { AddTramsComponent } from 'app/arees/trams/add-trams/add-trams.component';
import { EditTramsComponent } from 'app/arees/trams/edit-trams/edit-trams.component';
import { AddEditHorariComponent } from 'app/arees/arees-configuracions/add-edit-horari/add-edit-horari.component';
import { RestricionsComponent } from 'app/arees/restricions/restricions.component';
import { AddEditRestricionsComponent } from 'app/arees/restricions/add-edit-restricions/add-edit-restricions.component';
import { GremisComponent } from 'app/gestio-gremi/gremis/gremis.component';
import { AgremiatsComponent } from 'app/gestio-gremi/agremiats/agremiats.component';
import { PermisosComponent } from 'app/gestio-gremi/permisos/permisos.component';
import { VehiclesComponent } from 'app/vehicles/vehicles.component';
import { AddEditVehiclesComponent } from 'app/vehicles/add-edit-vehicles/add-edit-vehicles.component';
import { AddEditAgremiatsComponent } from 'app/gestio-gremi/agremiats/add-edit-agremiats/add-edit-agremiats.component';
import { AddEditPermisosComponent } from 'app/gestio-gremi/permisos/add-edit-permisos/add-edit-permisos.component';
import { GestioTargetasComponent } from 'app/gestio-gremi/gestio-targetas/gestio-targetas.component';
import { OperacionsTargetaComponent } from 'app/gestio-gremi/operacions-targeta/operacions-targeta.component';
import { UsuarisBoComponent } from 'app/sistema/usuaris-bo/usuaris-bo.component';
import { EditUsuarisBoComponent } from 'app/sistema/edit-usuaris-bo/edit-usuaris-bo.component';
import { RolsComponent } from 'app/sistema/rols/rols.component';
import { ParametresComponent } from 'app/sistema/parametres/parametres.component';
import { CondicionsDUsComponent } from 'app/sistema/condicions-d-us/condicions-d-us.component';
import { PolitiquesPComponent } from 'app/sistema/politiques-p/politiques-p.component';
import { LlistatnotificacionsComponent } from 'app/notificacions/llistat-notificacions.component';
import { EnviamentpushComponent } from 'app/notificacions/enviament-push/enviament-push.component';
import { DUMRoutes } from 'app/app.routing';
import { APLICATION } from '../../globalVariables/globalVariables';
import { VehiclesBusComponent } from 'app/vehicles-bus/vehicles-bus.component';

declare const $: any;

export interface MenuItem {
    path?: string,
    key: string,
    title: string,
    icon: string,
    component?: any,
    permission?: any,
    childrenVisible?: boolean,
    class?: string,
    children?: MenuItemChild[]
    application?:any,
}

export interface MenuItemChild {
    path: string,
    title: string,
    component: any,
    class?: string,
    permission?: any
}

export const DUM_MENU_ITEMS: MenuItem[] = [
    {
        path: '/inici',
        key: 'inici',
        title: 'MENUITEM.HOME',
        icon: 'home',
        children: undefined,
        childrenVisible: false,
        component: DashboardComponent
    },
    {
        path: '/gestio-operativa',
        key: 'gestio-operativa',
        title: 'MENUITEM.USER_MANAGEMENT',
        icon: 'person_pin',
        children: undefined,
        childrenVisible: false,
        permission: FUNCIONALITIES.USER_QUERY,
        component: [GestioOperativaComponent, EditUsersComponent]
    },
    {
        path: '/cercador-operacions',
        key: 'cercador-operacions',
        title: 'MENUITEM.OPERATIONS_QUERY',
        icon: 'search',
        children: undefined,
        childrenVisible: false,
        permission: FUNCIONALITIES.OPERATIONS_QUERY,
        component: CercadorOperacionsComponent
    },
    {
        title: 'MENUITEM.AREA',
        key: 'arees',
        icon: 'room',
        children: [
            {
                path: '/arees/configuracions',
                title: 'MENUITEM.AREA_CONFIG',
                component: [AreesConfiguracionsComponent, AddEditHorariComponent]
            },
            {
                path: '/arees/trams',
                title: 'MENUITEM.AREA_SEGMENTS',
                component: [TramsComponent, AddTramsComponent, EditTramsComponent]
            },
            {
                path: '/arees/restricions',
                title: 'MENUITEM.AREA_RES',
                component: [RestricionsComponent, AddEditRestricionsComponent]
            }
        ],
        childrenVisible: false,
        permission: FUNCIONALITIES.AREA_QUERY,
        application:APLICATION.ZONA_DUM
    },
    /*{
        title: 'MENUITEM.WATCHER',
        key: 'vigilants',
        icon: 'stars',
        permission: FUNCIONALITIES.WATCHER_QUERY,
        children: [
            {
                path: '/vigilants/gestio',
                title: 'MENUITEM.WATCHER_MANAGEMENT',
                permission: FUNCIONALITIES.WATCHER_QUERY,
                component: [GestioVigilantsComponent, AddEditVigilantsComponent]
            },
            {
                path: '/vigilants/notificacions',
                title: 'MENUITEM.WATCHER_NOTIFICATIONS',
                permission: FUNCIONALITIES.WATCHER_MANAGEMENT,
                component: [NotificacionsVigilantsComponent]
            }
        ]
    },*/
    {
        path: '/vehicles',
        key: 'vehicles',
        title: 'MENUITEM.VEHICLES',
        icon: 'local_shipping',
        children: undefined,
        childrenVisible: false,
        permission: FUNCIONALITIES.VEHICLES_QUERY,
        component: [VehiclesComponent, AddEditVehiclesComponent],
        application:APLICATION.ZONA_DUM
    },
    {
        path: '/vehicles-bus',
        key: 'vehicles',
        title: 'MENUITEM.VEHICLES',
        icon: 'directions_bus',
        children: undefined,
        childrenVisible: false,
        permission: FUNCIONALITIES.VEHICLES_QUERY,
        component: [VehiclesBusComponent, AddEditVehiclesComponent],
        application:APLICATION.ZONA_BUS
    },
    {
        key: 'gestio-gremi',
        title: 'MENUITEM.GUILDS',
        icon: 'group',
        children: [
            {
                path: '/gestio-gremi/gremis',
                title: 'MENUITEM.GUILDS_MANAGEMENT',
                component: [GremisComponent, AddEditVehiclesComponent]
            },
            {
                path: '/gestio-gremi/agremiats',
                title: 'MENUITEM.GUILD_MEMBERS',
                component: [AgremiatsComponent, AddEditAgremiatsComponent]
            },
            {
                path: '/gestio-gremi/permisos',
                title: 'MENUITEM.PERMISSIONS',
                component: [PermisosComponent, AddEditPermisosComponent]
            },
            {
                path: '/gestio-gremi/gestio-targetas/',
                title: 'MENUITEM.CARDS',
                component: GestioTargetasComponent
            },
			{
				path: '/gestio-gremi/operacions-targeta/',
				title: 'MENUITEM.OPERATIONS',
				component: OperacionsTargetaComponent
			}
        ],
        permission: FUNCIONALITIES.GUILD_QUERY,
        childrenVisible: true,
        application:APLICATION.ZONA_DUM
    },
    {
        title: 'MENUITEM.SYSTEM',
        key: 'sistema',
        icon: 'settings_applications',
        permission: [FUNCIONALITIES.SYSTEM_ADMIN, FUNCIONALITIES.USERBO_QUERY],
        children: [
            {
                path: '/sistema/usuaris-bo',
                title: 'MENUITEM.USERS_BO',
                permission: FUNCIONALITIES.USERBO_QUERY,
                component: [UsuarisBoComponent, EditUsuarisBoComponent]
            },
            {
                path: '/sistema/rols',
                title: 'MENUITEM.ROLES',
                permission: FUNCIONALITIES.SYSTEM_ADMIN,
                component: RolsComponent
            },
            {
                path: '/sistema/parametres',
                title: 'MENUITEM.PARAMS',
                permission: FUNCIONALITIES.SYSTEM_ADMIN,
                component: ParametresComponent
            },
            {
                path: '/sistema/condicions',
                title: 'MENUITEM.CONDITIONS',
                permission: FUNCIONALITIES.SYSTEM_ADMIN,
                component: CondicionsDUsComponent
            },
            {
                path: '/sistema/pp',
                title: 'MENUITEM.PP',
                permission: FUNCIONALITIES.SYSTEM_ADMIN,
                component: PolitiquesPComponent
            }
        ],
        childrenVisible: false,
        application:APLICATION.ZONA_DUM
    },
    {
        title: 'MENUITEM.NOTS',
        key: 'notificacions',
        icon: 'notifications',
        permission: FUNCIONALITIES.NOTIFICATIONS_MANAGEMENT,
        children: [
            {
                path: '/notificacions/llista',
                title: 'MENUITEM.NOTS_LIST',
                component: LlistatnotificacionsComponent
            },
            {
                path: '/notificacions/push',
                title: 'MENUITEM.PUSH_SENDER',
                component: EnviamentpushComponent
            }
        ],
        childrenVisible: false
    }
];



@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
    public menuItems: MenuItem[];
    public url: string;
    public urlAll: string;
    public version = environment.version;

    constructor(
        public curUser: CurrentUserService, public navigation: NavigationEndInterceptor,
        private router: Router, public auth: AuthorizationService) {
    }

    ngOnInit() {
        this.menuItems = DUM_MENU_ITEMS;
        this.menuItems.forEach((menuItem: MenuItem) => {
            if (this.checkActiveParent(menuItem)) {
                menuItem.childrenVisible = true;
            } else {
                menuItem.childrenVisible = false;
            }
        });
    }

    public isMobileMenu(): boolean {
        if ($(window).width() > 991) {
            return false;
        }
        return true;
    };

    public toggleChildren(menuItem): void {
        menuItem.childrenVisible = !menuItem.childrenVisible;
    }

    public isForBarcelona(child): boolean {
        if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
            if (child.key === 'arees') {
                return false;
            }
        }
        return true;
    }

    public isAppliaction(child): boolean {
        if(child.application === undefined || child.application == this.curUser.getProject() ){
            return true;
        }
        return false;
    }

    public checkActiveParent(menuItem: MenuItem): boolean {
        if (menuItem) {
            if (menuItem.children) {
                let activeChild = false;
                for (let index = 0; index < menuItem.children.length && !activeChild; index++) {
                    activeChild = this.checkActiveChild(menuItem.children[index], menuItem);
                }
                if (activeChild) {
                    this.navigation.currentMenuItemParent = menuItem;
                    return true;
                }
            }
            const dumRoute: DUMRoutes = this.navigation.currentDumRoute;
            if (dumRoute && menuItem.component) {
                if (Array.isArray(menuItem.component)) {
                    let isSameComponent = false;
                    for (let index = 0; index < menuItem.component.length && !isSameComponent; index++) {
                        isSameComponent = menuItem.component[index] === dumRoute.component;
                    }
                    if (isSameComponent) {
                        this.navigation.currentMenuItemChild = undefined;
                        this.navigation.currentMenuItemParent = menuItem
                        return true;
                    }
                } else {
                    if (menuItem.component === dumRoute.component) {
                        this.navigation.currentMenuItemChild = undefined;
                        this.navigation.currentMenuItemParent = menuItem;
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public checkActiveChild(menuItemChild: MenuItemChild, menuItem: MenuItem): boolean {
        const dumRoute: DUMRoutes = this.navigation.currentDumRoute;
        if (dumRoute && menuItemChild.component) {
            if (Array.isArray(menuItemChild.component)) {
                let isSameComponent = false;
                for (let index = 0; index < menuItemChild.component.length && !isSameComponent; index++) {
                    isSameComponent = menuItemChild.component[index] === dumRoute.component;
                }
                if (isSameComponent) {
                    this.navigation.currentMenuItemChild = menuItemChild;
                    return true;
                }
            } else {
                if (menuItemChild.component === dumRoute.component) {
                    this.navigation.currentMenuItemChild = menuItemChild;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Color Menu esta actiu
     * @param menuItem 
     */
    public getColorSproBus(menuItem: MenuItem){
        let color = "";
        let boolean = this.checkActiveParent(menuItem)
        if(boolean){
            if(this.curUser.isSPRO()){
                color = "active"
            }else{
                color = "activeBus"
            }
        }
        return color;
    }

    /**
     * Color Menu esta actiu
     * @param menuItem 
     */
    public getChildColorSproBus(menuItemChild: MenuItemChild, menuItem: MenuItem){
        let color = "";
        let boolean = this.checkActiveChild(menuItemChild,menuItem)
        if(boolean){
            if(this.curUser.isSPRO()){
                color = "activeChild"
            }else{
                color = "activeChildBus"
            }
        }
        return color;
    }
    
    
}
