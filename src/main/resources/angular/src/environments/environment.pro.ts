import { version } from './version';

export const environment = {
    production: true,
    baseHref : '%BASE_HREF%',
    api : '%API%',
    version : version,
    geoserverPath : '%GEOSERVER_PATH%',
    geoserverWFS : '%GEOSERVER_WFS%',
    geoserverUser : '%GEOSERVER_USER%',
    geoserverPass : '%GEOSERVER_PASS%',
    layerWMS : '%LAYER_WMS%',
    clientId : '%CLIENT_ID%',
    clientSecret : '%CLIENT_SECRET%'
};
