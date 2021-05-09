import { version } from './version';

export const environment = {
    production: true,
    baseHref : '/bo/',
    api : 'https://dum.nexusgeografics.com/api/snapshot/v1',
    version : version,
    geoserverPath : 'http://maas.nexusgeografics.com/geoserver/maas/ows',
    geoserverWFS : 'https://maas.nexusgeografics.com/geoserver/maas/wfs',
    geoserverUser : 'editor',
    geoserverPass : 'editor',
    layerWMS : 'http://geoserveis.icc.cat/icc_mapesmultibase/noutm/wms/service?',
    clientId : 'bo',
    clientSecret : 'secret'
}
