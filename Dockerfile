# Build Stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Production Stage
FROM openjdk:17.0.1-jdk-slim
LABEL authors="Yoramu"
COPY --from=build /target/OnlineMarketplaceAPI-0.0.1-SNAPSHOT.jar OnlineMarketplaceAPI.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OnlineMarketplaceAPI.jar"]
