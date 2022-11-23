FROM amazoncorretto:11-al2-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY src src
COPY mvnw .
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

FROM amazoncorretto:11-al2-jdk
ENV JAVA_ARGS=""
WORKDIR /app
COPY --from=build /app/target/*.jar ./app.jar
ENTRYPOINT java $JAVA_ARGS -jar app.jar