import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastService } from '../../../services/toast.service';
import { CityConfigService } from '../../../api/api/cityConfig.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { DataService } from '../../../api/api/data.service';
import { AuthorizationService } from '../../../services/authorization.service';
import { MapService } from '../../../services/map.service';
import { FormControl, Validators } from '@angular/forms';
import { Select } from 'ol/interaction';
import { Style, Stroke } from 'ol/style';
import { RestrictionConf } from '../../../api/model/restrictionConf';
import { SegmentConf } from '../../../api/model/segmentConf';
import { TranslateService } from '@ngx-translate/core';
import { config } from 'config/config';

@Component({
  selector: 'app-add-edit-restricions',
  templateUrl: './add-edit-restricions.component.html',
  styleUrls: ['./add-edit-restricions.component.scss']
})
export class AddEditRestricionsComponent implements AfterViewInit {

  public params;
  @ViewChild('gmap') public gmapElement;
  public addingNew: boolean;
  public rest: RestrictionConf = {
    restrictionId: -1,
    segments: [],
    description: '',
    startDate: null,
    endDate: null
  };
  public loadingMap;
  public initialized = false;
  public map: google.maps.Map;
  public trams: Array<google.maps.Marker>;
  public name = new FormControl('', [Validators.required]);
  public startDate = new FormControl('', [Validators.required]);
  public endDate = new FormControl('', [Validators.required]);
  public currentInteraction = null;
  public selectedTrams: Array<google.maps.Marker> = [];
  public savingRes = false;

  constructor(
    private mapService: MapService, private translate: TranslateService,
    private router: Router,
    private route: ActivatedRoute, private toast: ToastService,
    private cityConf: CityConfigService, private curUser: CurrentUserService,
    private data: DataService, private auth: AuthorizationService
  ) { }

  ngAfterViewInit() {
    if (this.curUser.getMunicipi() === config.MUNICIPI_BARCELONA_ID) {
      this.router.navigateByUrl('/inici');
    }
    this.route.params.subscribe((params) => {
      this.addingNew = params.id === undefined;;
      this.map = new google.maps.Map(this.gmapElement.nativeElement,
        this.mapService.getMapProp(parseFloat('41.97644234'), parseFloat('2.808433769999965'), parseFloat('15')));
      if (!this.addingNew) {
        this.cityConf.restriction(this.curUser.getMunicipi(), params.id).subscribe((restriction: RestrictionConf) => {
          this.rest = restriction;
          this.name.patchValue(restriction.description);
          this.startDate.patchValue(restriction.startDate);
          this.endDate.patchValue(restriction.endDate);
          this.loadSegments(restriction.segments);
        });
      } else {
        this.loadSegments();
      }
    });

  }

  loadSegments(segmentsToSelect?: Array<SegmentConf>) {
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
              lng = coord.split(' ')[0];
              lat = coord.split(' ')[1];
              const marker = new google.maps.Marker({
                map: this.map,
                draggable: false,
                position: { lat: parseFloat(lat), lng: parseFloat(lng) }
              });
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
              marker.setOpacity(0.5);
              let selected = false;
              if (segmentsToSelect) {
                for (let i = 0; i < segmentsToSelect.length && !selected; i++) {
                  selected = segmentsToSelect[i].segmentId === marker.get('ID');
                }
                if (selected) {
                  this.selectTram(marker);
                }
              }
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

              marker.set('window', infowindow);
              
              marker.addListener('mouseover', () => {
                infowindow.open(this.map, marker);
              });

              // assuming you also want to hide the infowindow when user mouses-out
              marker.addListener('mouseout', function () {
                infowindow.close();
              });

              marker.addListener('click', () => {
                this.selectTram(marker);
              });
              this.trams.push(marker);
            }

          }
        });
        if (this.trams[0]) {
          this.map.setCenter(this.trams[0].getPosition());
        }
      }
    });
  }

  selectTram(tram: google.maps.Marker) {
    if (tram.get('selected')) {
      tram.set('selected', false);
      this.selectedTrams = this.selectedTrams.filter((selectedTram) => {
        return selectedTram.get('ID') !== tram.get('ID');
      });
      tram.setOpacity(0.5);
    } else {
      tram.set('selected', true);
      tram.setOpacity(1);
      this.selectedTrams.push(tram);
    }
  }

  centerTram(tram: google.maps.Marker) {
    this.map.setCenter(tram.getPosition());
    tram.get('window').open(this.map, tram);
  }

  isDisabledSave(): boolean {
    return this.selectedTrams.length === 0 || this.name.invalid || this.startDate.invalid || this.endDate.invalid;
  }

  saveRes() {
    this.savingRes = true;
    this.rest.description = this.name.value;
    this.rest.startDate = this.startDate.value;
    this.rest.endDate = this.endDate.value;
    this.rest.segments = [];
    this.selectedTrams.filter((item) => {
      const newSegment: SegmentConf = {
        segmentId: item.get('ID'),
        segmentName: item.get('NAME'),
        scheduleId: item.get('scheduleId'),
        cityId : this.curUser.getMunicipi(),
        maxTime : item.get('maxTime'),
        places : item.get('places'),
        maxTimeInfinity : item.get('maxTimeInfinity'),
        description : item.get('desc'),
        zoneTypeId : item.get('zoneTypeId'),
        segmentCode : item.get('segmentCode')
      }
      this.rest.segments.push(newSegment);
    });
    if (this.addingNew) {
      this.cityConf.createRestriction(this.curUser.getMunicipi(), this.rest).subscribe((ok) => {
        this.savingRes = false;
        this.translate.get('AREES.AE_RES.SAVED_CORRECTLY').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/arees/restricions']);
        });
      }, (err) => {
        this.savingRes = false;
        this.toast.error(err);
      });
    } else {
      this.cityConf.editRestriction(this.curUser.getMunicipi(), this.rest.restrictionId, this.rest).subscribe((ok) => {
        this.savingRes = false;
        this.translate.get('AREES.AE_RES.SAVED_CORRECTLY').subscribe((message) => {
          this.toast.notify('success', message);
          this.router.navigate(['/arees/restricions']);
        });
      }, (err) => {
        this.savingRes = false;
        this.toast.error(err);
      })
    }
  }

}
