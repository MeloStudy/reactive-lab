# Stage 1: Build
FROM docker.io/library/maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy POM and source
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir data
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
