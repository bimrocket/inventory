#!/bin/sh

if [ -z ${KAFKA_URL} ] || [ -z ${POSTGRES_URL} ] || [ -z ${POSTGRES_USER} ] || [ -z ${POSTGRES_PASSWORD} ]; then
  echo "ERROR: S'han de definir totes les variables d'entorn: KAFKA_URL, POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD"
  exit 1
fi

echo "**********************"
echo "Environtment variables"
echo "**********************"
echo ""
echo "KAFKA_URL=${KAFKA_URL}"
echo "POSTGRES_URL=${POSTGRES_URL}"
echo "POSTGRES_USER=${POSTGRES_USER}"
echo "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}"
echo ""

echo "Starting Springboot app ... ${JAR_FILE}"

exec java \
  -Dkafka.bootstrap.address=${KAFKA_URL} \
  -Dspring.kafka.bootstrap-servers=${KAFKA_URL} \
  -Dspring.datasource.url=${POSTGRES_URL} \
  -Dspring.datasource.username=${POSTGRES_USER} \
  -Dspring.datasource.password=${POSTGRES_PASSWORD} \
  -jar ${JAR_FILE}
