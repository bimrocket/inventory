#!/bin/sh

if [ -z ${KAFKA_URL} ] || [ -z ${POSTGRES_URL} ] || [ -z ${POSTGRES_USER} ] || [ -z ${POSTGRES_PASSWORD} ]; then
  echo "ERROR: S'han de definir totes les variables d'entorn: KAFKA_URL, POSTGRES_URL, POSTGRES_USER, POSTGRES_PASSWORD"
  exit 1
fi

echo exec java \
  -Dkafka.bootstrap.address=${KAFKA_URL} \
  -Dspring.kafka.bootstrap-servers=${KAFKA_URL} \
  -Dspring.datasource.url=${POSTGRES_URL} \
  -Dspring.datasource.username=${POSTGRES_USER} \
  -Dspring.datasource.password=${POSTGRES_PASSWORD} \
  -jar ${JAR_FILE} 

exec java \
  -Dkafka.bootstrap.address=${KAFKA_URL} \
  -Dspring.kafka.bootstrap-servers=${KAFKA_URL} \
  -Dspring.datasource.url=${POSTGRES_URL} \
  -Dspring.datasource.username=${POSTGRES_USER} \
  -Dspring.datasource.password=${POSTGRES_PASSWORD} \
  -jar ${JAR_FILE}
