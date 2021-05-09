# Replace environment variables
if [ -z ${BASE_HREF} ] || [ -z ${API} ] || [ -z ${LAYER_WMS} ] || [ -z ${CLIENT_ID} ] || [ -z ${CLIENT_SECRET} ] ; then
    echo "ERROR: S'han de definir totes les variables d'entorn: BASE_HREF, API, LAYER_WMS, CLIENT_ID, CLIENT_SECRET"
    exit 1
fi

APPJS=/var/www/bo/main.*.js

echo "**********************"
echo "Environtment variables"
echo "**********************"
echo ""
echo "BASE_HREF=$BASE_HREF"
echo "API=$API"
echo "LAYER_WMS=$LAYER_WMS"
echo "CLIENT_ID=$CLIENT_ID"
echo "CLIENT_SECRET=$CLIENT_SECRET"

echo "Replacing variables ..."
echo ""

# Replacing
sed -i -e "s#%BASE_HREF%#$BASE_HREF#g
           s#%API%#$API#g
           s#%LAYER_WMS%#$LAYER_WMS#g
           s#%CLIENT_ID%#$CLIENT_ID#g
           s#%CLIENT_SECRET%#$CLIENT_SECRET#g" ${APPJS}

echo "Launching nginx ..."

nginx -g 'daemon off;'
