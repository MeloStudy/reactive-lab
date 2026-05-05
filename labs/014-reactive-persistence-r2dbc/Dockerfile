FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/014-reactive-persistence-r2dbc-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
