import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import Map from 'ol/Map';
import View from 'ol/View';

import OSM from 'ol/source/OSM';

import { fromLonLat } from 'ol/proj';
import VectorSource from 'ol/source/Vector';
import OlVectorLayer from 'ol/layer/Vector';
import GeoJSON from 'ol/format/GeoJSON';
import OlTileLayer from 'ol/layer/Tile';
import TileWMS from 'ol/source/TileWMS';
import TileLayer from 'ol/layer/Tile.js';
import XYZ from 'ol/source/XYZ';
import VectorLayer from 'ol/layer/Vector';
import Vector from 'ol/source/Vector';
import Circle from 'ol/geom/Circle';
import { Style, Stroke, Fill } from 'ol/style';
import Geolocation from 'ol/Geolocation';
import Feature from 'ol/Feature';
import Overlay from 'ol/Overlay';
import CircleStyle from 'ol/style/Circle';
import Point from 'ol/geom/Point';
import { TicketsService } from '../../api';
import { AuthorizationService } from '../../services/authorization.service';
import { ToastService } from '../../services/toast.service';


@Component({
  selector: 'app-dialog-posicio-tiquet',
  templateUrl: './dialog-posicio-tiquet.component.html',
  styleUrls: ['./dialog-posicio-tiquet.component.scss']
})
export class DialogPosicioTiquetComponent implements OnInit {

  private view: View;
  private layer: TileLayer;
  public map: Map;
  public lon;
  public lat;
  public invalidTicket = false;
  public loading = true;

  constructor(
    private toast: ToastService,
    private tickets: TicketsService, private auth: AuthorizationService,
    private thisDialogRef: MatDialogRef<DialogPosicioTiquetComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    this.tickets.ticketDetailed(this.data.ticket.ticketId, this.auth.getBearer()).subscribe((ticketDetailed) => {
      this.lon = ticketDetailed.segmentLng;
      this.lat = ticketDetailed.segmentLat;
      if (!this.lat || !this.lon) {
        this.invalidTicket = true;
        this.loading = false;
      } else {
        this.initMapa();
        this.loading = false;
      }
    }, (err) => {
      this.toast.error(err);
      this.loading = false;
    })
  }

  initMapa() {
    const position = fromLonLat([this.lon, this.lat]);

    this.view = new View({
      center: position,
      zoom: 15,
      minZoom: 12,
      maxZoom: 18
    });

    this.layer = new TileLayer({
      source: new XYZ({
        url: 'https://tilemaps.icgc.cat/mapfactory/wmts/topo_suau/CAT3857/{z}/{x}/{y}.png',
      })
    });

    const _myStroke = new Stroke({
      color : 'rgba(255,255,255,1.0)',
      width : 2,
    });

    const _myFill = new Fill({
       color: 'rgba(0,0,0,1)'
    });

    const marker = new Overlay({
      position: fromLonLat([this.lon, this.lat]),
      element: document.getElementById('location')
    });
    /*
    const vectorSource = new Vector();
    const circleLayer = new VectorLayer({
      source: vectorSource,
      style : new Style({
        stroke : _myStroke,
        fill : _myFill
      })
    });

    vectorSource.addFeature(
      new Feature(
        new Circle(
          fromLonLat([this.lon, this.lat]), 50
        )
      )
    );
    */
    /*Nou mapa*/
    this.map = new Map({
      controls: [],
      layers: [
        this.layer
      ],
      target: 'map',
      view: this.view,
      minZoom: 12
    });
    this.map.addOverlay(marker);
    // window.map = this.mapa;
  }


  onCloseConfirm() {
    this.thisDialogRef.close('Confirm');
  }

  onCloseCancel() {
    this.thisDialogRef.close('Cancel');
  }
}
