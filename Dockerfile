FROM eclipse-temurin:11-jdk-alpine

ARG WORKING_DIRECORY=/app

RUN addgroup -S spring && adduser -S spring -G spring

USER spring:spring

COPY target/*.jar ${WORKING_DIRECORY}/app.jar

WORKDIR ${WORKING_DIRECORY}
ENTRYPOINT ["java","-jar","app.jar"]