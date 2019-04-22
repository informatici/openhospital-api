FROM openjdk:8-jre

ARG JAR_FILE

VOLUME /logs

RUN mkdir /app
ADD target/${JAR_FILE} /app/openhospital-api.jar

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java", "-jar", "/app/openhospital-api.jar"]