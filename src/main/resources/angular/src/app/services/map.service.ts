import { Injectable } from '@angular/core';
import * as proj from 'ol/proj';
import * as extent from 'ol/extent';
import * as control from 'ol/control';
import Map from 'ol/Map';
import View from 'ol/View';
import { Tile as TileLayer, Vector as VectorLayer } from 'ol/layer';
import TileWMS from 'ol/source/TileWMS';
import VectorSource from 'ol/source/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import { all as allStrategy } from 'ol/loadingstrategy';
import { Stroke, Style } from 'ol/style';
import { CityConfigService } from '../api/api/cityConfig.service';
import { CurrentUserService } from './current-user.service';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { WFS, GML } from 'ol/format';
import { ZoomSlider } from 'ol/control';
import { DataService } from '../api/api/data.service';
import { AuthorizationService } from './authorization.service';
import { ZoneType } from '../api';
import {} from '@types/googlemaps';


@Injectable()
export class MapService {

    private zoneTypes: Array<ZoneType>;
    public DUM_COLOR = 'yellow';
    public DUM_SELECTED_COLOR = 'red';
    public DUM_WIDTH = 5;

    constructor(
        private dataService: DataService, private auth: AuthorizationService,
        private cityConf: CityConfigService, private curUser: CurrentUserService) {
    }

    public loadTrams(map: google.maps.Map, opacity: number): Observable<google.maps.Data.Feature[]> {
        const observable = Observable.create((observer) => {
            map.data.loadGeoJson(environment.geoserverPath +
                '?service=WFS&version=1.1.0&request=GetFeature' +
                '&typeName=maas:SEGMENT&outputFormat=application%2Fjson&sortBy=NAME' +
                '&CQL_FILTER=DELETED_DATE%20is%20null', null, (features: google.maps.Data.Feature[]) => {
                    const trams: Array<google.maps.Data.Feature> = [];
                    features.forEach((feature: google.maps.Data.Feature) => {
                        if (feature.getProperty('DELETED_DATE') == null &&
                            feature.getProperty('CITY_ID') == this.curUser.getMunicipi()) {
                            trams.push(feature);
                        }
                    });
                    observer.next(trams);
                    observer.complete();
                });
            map.data.setStyle((feature: google.maps.Data.Feature) => {
                if (feature.getProperty('CITY_ID') != this.curUser.getMunicipi()) {
                    return {
                        visible: false
                    };
                } else if (feature.getProperty('selected')) {
                    return {
                        strokeOpacity : 1,
                        strokeColor: this.DUM_SELECTED_COLOR,
                        strokeWeight : this.DUM_WIDTH
                    }
                } else {
                    return {
                        strokeOpacity : opacity,
                        strokeColor : this.DUM_COLOR,
                        strokeWeight : this.DUM_WIDTH
                    }
                }
            });
        });
        return observable;
    }

    public getMapProp(lat: number, long: number, zoom: number) {
        return {
            center: new google.maps.LatLng(lat, long),
            zoom: zoom,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        }
    }

    hexToRgbA(hex, opacity) {
        let c;
        if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
            c = hex.substring(1).split('');
            if (c.length == 3) {
                c = [c[0], c[0], c[1], c[1], c[2], c[2]];
            }
            c = '0x' + c.join('');
            return 'rgba(' + [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(',') + ',' + opacity + ')';
        }
        throw new Error('Bad Hex');
    }
}