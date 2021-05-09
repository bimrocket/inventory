import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MapService } from '../../../services/map.service';
import { ConfigurationConf } from '../../../api/model/configurationConf';
import { FormControl, Validators } from '@angular/forms';
import { ToastService } from '../../../services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CityConfigService } from '../../../api/api/cityConfig.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { environment } from '../../../../environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { interval } from 'rxjs';
import { FakeDataService } from '../../../services/fake-data.service';
import { SegmentsService } from '../../../api/api/segments.service';
import { SegmentConf } from '../../../api/model/segmentConf';
import { ScheduleCityConf } from '../../../api/model/scheduleCityConf';
import { AuthorizationService } from '../../../services/authorization.service';
import { MatSlideToggleChange } from '@angular/material';
import { DataService } from '../../../api/api/data.service';
import { ZoneType } from '../../../api';
import { config } from 'config/config';

declare var $: any;

@Component({
  selector: 'app-add-trams',
  templateUrl: './add-trams.component.html',
  styleUrls: ['./add-trams.component.scss']
})
export class AddTramsComponent implements AfterViewInit {

  @ViewChild('gmap') gmapElement: any;
  public loadingMap = true;
  public tram: google.maps.Marker;
  public trams;
  public currentInteraction;
  public infoMap;
  public originalTram;
  public map: google.maps.Map;
  public params = {
    id: -1
  };
  public savingTram = false;
  public configs: Array<ScheduleCityConf>;
  public name = new FormControl('', [Validators.required]);
  public desc = new FormControl('', [Validators.required]);
  public places = new FormControl('', [Validators.required]);
  public configuration = new FormControl('', [Validators.required]);
  public maxTime = new FormControl('', [Validators.required]);
  public maxTimeInfinity = new FormControl(false);
  public zoneType = new FormControl('', Validators.required);
  public segmentCode = new FormControl('', Validators.required);
  public zoneTypes: ZoneType[];

  constructor(
    private auth: AuthorizationService, private data: DataService,
    private router: Router, private fakeDataService: FakeDataService,
    private toast: ToastService, private translate: TranslateService,
    private route: ActivatedRoute, private cityConf: CityConfigService,
    private mapService: MapService, private curUser: CurrentUserService,
    private segmentService: SegmentsService
  ) { }

  ngAfterViewInit() {
    if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
      this.router.navigateByUrl('/inici');
    }
    const x = this.route.snapshot.queryParamMap.get('lat');
    const y = this.route.snapshot.queryParamMap.get('long');
    const zoom = this.route.snapshot.queryParamMap.get('zoom');
    this.cityConf.schedules(this.curUser.getMunicipi()).subscribe((horaris) => {
      this.configs = horaris;
    });
    this.data.getAllZoneTypes(this.auth.getBearer()).subscribe((zoneTypes) => {
      this.zoneTypes = zoneTypes;
    });
    this.map = new google.maps.Map(this.gmapElement.nativeElement,
      this.mapService.getMapProp(parseFloat(x), parseFloat(y), parseFloat(zoom)));
    this.tram = new google.maps.Marker({
      position: this.map.getCenter(),
      map: this.map,
      title: 'Tram actual',
      draggable: true
    });
    let changePositon = false;
    this.cityConf.segments(this.curUser.getMunicipi()).subscribe((segments) => {
      if (segments) {
        this.trams = [];
        segments.forEach((segment: SegmentConf) => {
          if (segment.geometry && !segment.deletedDate) {
            let lat, lng;
            const splittedgeo = segment.geometry.split('(');
            if (splittedgeo[0] === 'POINT ') {
              let coord: string = splittedgeo[1];
              coord = coord.substring(0, coord.length - 2);
              lng = parseFloat(coord.split(' ')[0]).toFixed(5);
              lat = parseFloat(coord.split(' ')[1]).toFixed(5);
              const marker = new google.maps.Marker({
                map: this.map,
                draggable: false,
                position: { lat: parseFloat(lat), lng: parseFloat(lng) }
              });
              if (lat === this.map.getCenter().lat().toFixed(5) && lng === this.map.getCenter().lng().toFixed(5)) { 
                changePositon = true;
              }
              marker.set('ID', segment.segmentId);
              marker.set('NAME', segment.segmentName);
              marker.set('desc', segment.description);
              marker.set('lat', parseFloat(lat));
              marker.set('lng', parseFloat(lng));
              marker.set('scheduleId', segment.scheduleId);
              marker.set('maxTime', segment.maxTime);
              marker.set('maxTimeInfinity', segment.maxTimeInfinity);
              marker.set('places', segment.places);
              marker.set('zoneTypeId', segment.zoneTypeId);
              const infowindow = new google.maps.InfoWindow({
                content: '<div id="content">' +
                  '<p><b> ID : </b>' + marker.get('ID') + '</p>' +
                  '<p><b> Nom : </b> ' + marker.get('NAME') + '</p>' +
                  '<p><b> Descripció : </b> ' + marker.get('desc') + '</p>' +
                  '<p><b> Lat : </b> ' + marker.get('lat') + '</p>' +
                  '<p><b> Lng : </b> ' + marker.get('lng') + '</p>' +
                  '<p><b> Temps màxim estacionament : </b>' +
                  (marker.get('maxTimeInfinity') ? 'Il·limitat' : marker.get('maxTime')) + '</p>' +
                  '<p><b> Places </b>' + marker.get('places') + '</p>' +
                  '</div>'
              });
              marker.addListener('mouseover', () => {
                infowindow.open(this.map, marker);
              });

              // assuming you also want to hide the infowindow when user mouses-out
              marker.addListener('mouseout', function () {
                infowindow.close();
              });
              marker.setOpacity(0.3);
              this.trams.push(marker);
            }

          }
        });
        if (changePositon) {
          const newPosition = new google.maps.LatLng(this.map.getCenter().lat() + 0.001, this.map.getCenter().lng() + 0.001);
          this.map.setCenter(newPosition);
          this.tram.setPosition(newPosition);
        }
      }
    });

  }

  clean() {
  }

  getZoneTypeName(zoneTypeId: number) {
    if (zoneTypeId === undefined) {
      return 'AREES.TRAMS.INVALID_CONFIG';
    } else {
      return this.zoneTypes.find((item) => {
        return item.zoneTypeId === zoneTypeId;
      }).description.cat;
    }
  }

  saveTram() {
    this.savingTram = true;
    const newTram: SegmentConf = {
      segmentId: 0,
      segmentName: this.name.value,
      description: this.desc.value,
      places: this.places.value,
      maxTime: this.maxTime.value,
      scheduleId: this.configuration.value,
      cityId: this.curUser.getMunicipi(),
      geometry: this.getWkt(),
      maxTimeInfinity: this.maxTimeInfinity.value,
      zoneTypeId: this.zoneType.value,
      segmentCode: this.segmentCode.value
    };
    this.segmentService.createSegments(this.auth.getBearer(), newTram).subscribe(() => {
      this.translate.get('AREES.ADD_TRAMS.CREATED').subscribe((message) => {
        this.toast.notify(this.toast.TOAST_TYPES.SUCCESS, message);
      })
      this.router.navigateByUrl('/arees/trams');

      this.savingTram = false;
    }, (err) => {

      this.savingTram = false;
      this.toast.error(err);
    })
  }

  private getWkt(): string {
    const pos: google.maps.LatLng = this.tram.getPosition();
    return 'POINT (' + pos.lng() + ' ' + pos.lat() + ')';
  }


}
