FROM eclipse-temurin:11-jdk-alpine

ARG WORKING_DIRECORY=/app

ENV JAVA_ARGS=""

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

COPY target/*.jar ${WORKING_DIRECORY}/app.jar

WORKDIR ${WORKING_DIRECORY}
ENTRYPOINT java $JAVA_ARGS -jar app.jar