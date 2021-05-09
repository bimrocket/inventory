import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { MapService } from '../../../services/map.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Select, Modify } from 'ol/interaction';
import { Stroke, Style } from 'ol/style';
import { Draw } from 'ol/interaction';
import { FormControl, Validators } from '@angular/forms';
import { CityConfigService } from '../../../api/api/cityConfig.service';
import { ToastService } from '../../../services/toast.service';
import { ConfigurationConf } from '../../../api/model/configurationConf';
import { environment } from 'environments/environment';
import { TranslateService } from '@ngx-translate/core';
import { ScheduleCityConf } from '../../../api/model/scheduleCityConf';
import { SegmentsService } from '../../../api/api/segments.service';
import { SegmentAll } from '../../../api/model/segmentAll';
import { AuthorizationService } from '../../../services/authorization.service';
import { MatSlideToggleChange } from '@angular/material';
import { SegmentConf } from '../../../api/model/segmentConf';
import { SegmentEdit } from '../../../api/model/segmentEdit';
import { DataService } from '../../../api/api/data.service';
import { ZoneType } from '../../../api';
import { config } from 'config/config';
declare var $: any;

@Component({
  selector: 'app-edit-trams',
  templateUrl: './edit-trams.component.html',
  styleUrls: ['./edit-trams.component.scss']
})
export class EditTramsComponent implements AfterViewInit {

  @ViewChild('gmap') gmapElement: any;
  public loadingMap = false;
  public tram: google.maps.Marker;
  public trams;
  public segment: SegmentAll;
  public currentInteraction;
  public map: google.maps.Map;
  public originalTram;
  public params;
  public savingTram = false;
  public loadingTram = true;
  public invalidConf = false;
  public configs: Array<ScheduleCityConf>;
  public name = new FormControl('', [Validators.required]);
  public codeSegment = new FormControl('', [Validators.required]);
  public desc = new FormControl('', [Validators.required]);
  public places = new FormControl('', [Validators.required]);
  public configuration = new FormControl('', [Validators.required]);
  public maxTime = new FormControl(0, [Validators.required]);
  public zoneType = new FormControl('', [Validators.required]);
  public maxTimeInfinity = new FormControl(false); 
  public initialized = false;
  public zoneTypes: ZoneType[];


  constructor(
    private auth: AuthorizationService, private data: DataService,
    private translate: TranslateService, private segmentService: SegmentsService,
    private toast: ToastService, private router: Router,
    private route: ActivatedRoute, private cityConf: CityConfigService,
    private mapService: MapService, private curUser: CurrentUserService
  ) { }

  ngAfterViewInit() {
    if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
      this.router.navigateByUrl('/inici');
    }
    this.data.getAllZoneTypes(this.auth.getBearer()).subscribe((zoneTypes) => {
      this.zoneTypes = zoneTypes;
    });
    this.route.params.subscribe((params) => {
      this.cityConf.schedules(this.curUser.getMunicipi()).subscribe((horaris) => {
        this.configs = horaris;
      });
      this.cityConf.segments(this.curUser.getMunicipi()).subscribe((segments) => {
        if (segments) {
          this.trams = [];
          segments.forEach((segment: SegmentConf) => {
            if (segment.geometry && !segment.deletedDate && segment.segmentId !== parseFloat(params.id)) {
              let lat, lng;
              const splittedgeo = segment.geometry.split('(');
              if (splittedgeo[0] === 'POINT ') {
                let coord: string = splittedgeo[1];
                coord = coord.substring(0, coord.length - 2);
                lng = coord.split(' ')[0];
                lat = coord.split(' ')[1];
                const marker = new google.maps.Marker({
                  map: this.map,
                  draggable: false,
                  position: { lat: parseFloat(lat), lng: parseFloat(lng) }
                });
                marker.set('ID', segment.segmentId);
                marker.set('SEGMENTCODE', segment.segmentCode);
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
                  content: '<div id="content">'+
                  '<p><b> ID : </b>' + marker.get('ID') + '</p>' + 
                  '<p><b> CODI : </b>' + marker.get('SEGMENTCODE') + '</p>' + 
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
                marker.addListener('mouseout', function() {
                    infowindow.close();
                });
                marker.setOpacity(0.3);
                this.trams.push(marker);
              }
  
            }
          });
        }
      });
      this.segmentService.getSegmentDetail(params.id, this.auth.getBearer()).subscribe((segment: SegmentAll) => {
        this.segment = segment;
        if (segment.geometry) {
          let lat, lng;
          const splittedgeo = segment.geometry.split('(');
          if (splittedgeo[0] === 'POINT ') {
            let coord: string = splittedgeo[1];
            coord = coord.substring(0, coord.length - 2);
            lng = coord.split(' ')[0];
            lat = coord.split(' ')[1];
            this.map = new google.maps.Map(this.gmapElement.nativeElement,
              this.mapService.getMapProp(parseFloat(lat), parseFloat(lng), 15));
            const marker = new google.maps.Marker({
              map: this.map,
              draggable: true,
              title: this.name.value,
              position: { lat: parseFloat(lat), lng: parseFloat(lng) }
            });
            this.tram = marker;
            this.name.patchValue(segment.name);
            this.codeSegment.patchValue(segment.segmentCode);
            this.desc.patchValue(segment.description);
            this.places.patchValue(segment.places);
            this.configuration.patchValue(segment.scheduleId);
            this.maxTime.patchValue(segment.maxTime);
            this.maxTimeInfinity.patchValue(segment.maxTimeInfinity);
            this.zoneType.patchValue(segment.zoneTypeId);
            this.tram = marker;
            this.loadingTram = false;
          }
        }

      }, (err) => {
        this.toast.error(err);
      });
    });

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
    const tram: SegmentEdit = {
      name : this.name.value,
      codeSegment : this.codeSegment.value,
      description : this.desc.value,
      maxTime : this.maxTime.value,
      maxTimeInfinity : this.maxTimeInfinity.value,
      places : this.places.value,
      scheduleId : this.configuration.value,
      cityId : this.segment.cityId,
      geometry : this.getWkt(),
      zoneTypeId : this.zoneType.value
    };
    this.segmentService.updateSegments(this.segment.segmentId.toString(), this.auth.getBearer(), tram).subscribe((ok) => {
      this.savingTram = false;
      this.router.navigate(['/arees/trams']);
      this.translate.get('AREES.EDIT_TRAMS.TRAM_EDITED').subscribe((message) => {
        this.toast.notify('success', message);
      });
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
