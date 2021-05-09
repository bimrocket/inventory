import { Component, OnInit, ViewChild } from '@angular/core';
import * as ol from 'ol';
import { MapService } from '../../services/map.service';
import { MatDialog } from '@angular/material';
import { DialogDetallsTramComponent } from './dialog-detalls-tram/dialog-detalls-tram.component';
import { CurrentUserService } from '../../services/current-user.service';
import { environment } from '../../../environments/environment';
import { CityConfigService } from '../../api/api/cityConfig.service';
import { ToastService } from '../../services/toast.service';
import { TranslateService } from '@ngx-translate/core';
import { DialogDeleteTramComponent } from './dialog-delete-tram/dialog-delete-tram.component';
import { ConfigurationConf } from '../../api/model/configurationConf';
import { DataService } from '../../api/api/data.service';
import { AuthorizationService } from '../../services/authorization.service';
import { ZoneType } from '../../api';
import { } from '@types/googlemaps';
import { SegmentsService } from '../../api/api/segments.service';
import { SegmentConf } from '../../api/model/segmentConf';
import { config } from 'config/config';
import { Router } from '@angular/router';
import { FUNCIONALITIES } from '../../globalVariables/globalVariables';

@Component({
  selector: 'app-trams',
  templateUrl: './trams.component.html',
  styleUrls: ['./trams.component.scss']
})
export class TramsComponent implements OnInit {

  @ViewChild('gmap') gmapElement: any;
  public loadingMap = true;
  public trams: Array<google.maps.Marker>;
  public originalTrams: Array<google.maps.Marker> = [];
  public map: google.maps.Map;
  public searchQuery: string;
  public init = false;
  public zoneTypes: Array<ZoneType>;
  public FUNCIONALITIES = FUNCIONALITIES;
  constructor(
    private segmentsSerivce: SegmentsService, private router: Router,
    private data: DataService, private auth: AuthorizationService,
    private toast: ToastService, private translate: TranslateService,
    public curUser: CurrentUserService, private cityConf: CityConfigService,
    private mapService: MapService, private dialog: MatDialog) { }

  ngOnInit() {
    const that = this;
    if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
      this.router.navigateByUrl('/inici');
    }
    this.data.getAllZoneTypes(this.auth.getBearer()).subscribe((zoneTypes) => {
      this.zoneTypes = zoneTypes;
      this.map = new google.maps.Map(this.gmapElement.nativeElement, this.mapService.getMapProp(41.502949, 1.978146, 15));
      this.cityConf.segments(this.curUser.getMunicipi()).subscribe((segments) => {
        if (segments) {
          this.originalTrams = [];
          segments.forEach((segment: SegmentConf) => {
            if (segment.geometry && !segment.deletedDate) {
              let lat,lng;
              const splittedgeo = segment.geometry.split('(');
              if (splittedgeo[0] === 'POINT ') {
                let coord: string = splittedgeo[1];
                coord = coord.substring(0, coord.length - 2);
                lng = coord.split(' ')[0];
                lat = coord.split(' ')[1];
                const marker = new google.maps.Marker({
                  map: this.map,
                  draggable: false,
                  position: {lat: parseFloat(lat), lng: parseFloat(lng)}
                });
                marker.set('ID', segment.segmentId);
                marker.set('SEGMENTCODE', segment.segmentCode);
                marker.set('NAME', segment.segmentName);
                marker.set('desc', segment.description);
                marker.set('selected', false);
                marker.set('lat', parseFloat(lat));
                marker.set('lng', parseFloat(lng));
                marker.set('scheduleId', segment.scheduleId);
                marker.set('maxTime', segment.maxTime);
                marker.set('maxTimeInfinity', segment.maxTimeInfinity);
                marker.set('places', segment.places);
                marker.set('zoneTypeId', segment.zoneTypeId);
                marker.addListener('click', () => {
                  this.selectRow(marker);
                });
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
                this.originalTrams.push(marker);
              }
              
            }
          });
          if (this.originalTrams[0]) {
            this.map.setCenter(this.originalTrams[0].getPosition());
          }
          this.trams = this.originalTrams;
          this.loadingMap = false;
        }
      });
    });
  }

  filterTrams(newValue) {
    if (newValue) {
      this.trams = this.originalTrams.filter((item) => {
        let a = item.get('NAME').toUpperCase().includes(
          newValue.toUpperCase());
        return a ? a : item.get('SEGMENTCODE').toString().includes(newValue);
      });
    } else {
      this.trams = this.originalTrams;
    }
  }

  deleteTram(tram: google.maps.Marker): void {
    const dialogRef = this.dialog.open(DialogDeleteTramComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe((info) => {
      if (info === 'Confirm') {
        this.cityConf.deleteSegment(this.curUser.getMunicipi(), tram.get('ID')).subscribe(() => {
          tram.setMap(null);
          this.originalTrams = this.originalTrams.filter((item) => {
            return item.get('ID') !== tram.get('ID');
          });
          this.trams = this.originalTrams;
          this.filterTrams(this.searchQuery);
          this.translate.get('AREES.TRAMS.DELETED').subscribe((message) => {
            this.toast.notify('success', message);
          });
        }, (err) => {
          this.toast.error(err);
        });
      }
    })
  }

  selectRow(tram: google.maps.Marker) {
    this.trams.filter((item) => {
      item.set('selected', false);
      return true;
    })
    tram.set('selected', true);
    this.map.setCenter(tram.getPosition());
    this.map.setZoom(18);
  }

  getCenter() {
    if (this.map) {
      return [this.map.getCenter().lat(), this.map.getCenter().lng()];
    } else {
      return [0, 0];
    }
  }

  getZoom() {
    if (this.map) {
      return this.map.getZoom()
    } else {
      return 15;
    }
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

  selectTram(tramId) {
    const tram = this.trams.filter((item) => {
      item.set('selected', false);
      return item.get('ID') == tramId;
    })[0];
    tram.set('selected', true);
    document.getElementById(tram.get('ID')).scrollIntoView();
  }

  obrirDetalls(tram) {
    const dialogRef = this.dialog.open(DialogDetallsTramComponent, {
      width: '600px',
      data: {
        tram: tram,
        zoneTypes: this.zoneTypes
      }
    });
  }

}
