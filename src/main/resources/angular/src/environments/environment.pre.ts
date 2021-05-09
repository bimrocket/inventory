import { version } from './version';

export const environment = {
    production: true,
    baseHref : '/bo/',
    api : '/api/v1',
    version : version,
    geoserverPath : 'http://172.17.17.7/geoserver/maas/ows',
    geoserverWFS : 'http://172.17.17.7/geoserver/maas/wfs',
    geoserverUser : 'editor',
    geoserverPass : 'ambEditor2019',
    layerWMS : 'http://geoserveis.icc.cat/icc_mapesmultibase/noutm/wms/service?',
    clientId : 'bo',
    clientSecret : 'secret'
};
