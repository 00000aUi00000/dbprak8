# development
FROM maven:3.8.5-openjdk-17-slim AS development
WORKDIR /backend
COPY pom.xml .
COPY src src
#RUN apt-get update -y && apt-get install -y maven
RUN mvn verify clean install --fail-never -DskipTests
EXPOSE 8080
CMD ["mvn", "spring-boot:run"]


# build
#FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
FROM development AS build
WORKDIR /backend
#maybe RUN mvn clean package -DskipTests
#(skips tests for missing database connection, faster build time and smaller image)
RUN mvn clean package -DskipTests 


# production
FROM openjdk:17.0.1-jdk-slim AS production
WORKDIR /backend
COPY --from=build /backend/target/. .
EXPOSE 8080
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
