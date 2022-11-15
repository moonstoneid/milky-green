FROM amazoncorretto:11-al2-jdk

ARG WORKING_DIRECORY=/app

ENV JAVA_ARGS=""

COPY target/*.jar ${WORKING_DIRECORY}/app.jar

WORKDIR ${WORKING_DIRECORY}
ENTRYPOINT java $JAVA_ARGS -jar app.jar