FROM adoptopenjdk/openjdk11:jre-11.0.6_10-alpine

ENV TZ Europe/Madrid
ENV JAR_FILE inventari-municipal-0.0.1-SNAPSHOT.jar

WORKDIR /opt

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/${JAR_FILE} /opt/

RUN addgroup -S spring && adduser -S spring -G spring

COPY docker/entrypoint.sh /usr/local/bin
RUN chmod +x /usr/local/bin/entrypoint.sh

USER spring:spring

EXPOSE 8091
ENTRYPOINT ["entrypoint.sh"]
