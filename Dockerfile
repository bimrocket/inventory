FROM openjdk:8-jdk-alpine

ENV TZ Europe/Madrid
ENV JAR_FILE inventari-municipal-0.0.1-SNAPSHOT.jar

WORKDIR /opt

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/${JAR_FILE} /opt/

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8091
ENTRYPOINT java -jar -Dspring-boot.run.profiles=nexus -Dspring.profiles.include=dev $JAR_FILE
