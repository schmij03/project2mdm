# Use the official maven/Java 11 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8-openjdk-11-slim AS build

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Use AdoptOpenJDK for base image.
FROM adoptopenjdk:11-jre-hotspot

# Copy the jar to the production image from the builder stage.
COPY --from=build /app/target/playgroundjan-1.0-SNAPSHOT.jar /playgroundjan-1.0-SNAPSHOT.jar

# Expose the port the app runs in
EXPOSE 8080

# Run the web service on container startup.
CMD ["java", "-jar", "/playgroundjan-1.0-SNAPSHOT.jar"]
