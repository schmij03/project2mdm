# Use the official maven/Java 8 image to create a build artifact.
# https://hub.docker.com/_/maven
FROM maven:3.8-openjdk-11-slim AS build

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Use AdoptOpenJDK for base image.
# It's important to use OpenJDK 8u191 or above that has container support enabled.
FROM adoptopenjdk:11-jre-hotspot

# Copy the jar to the production image from the builder stage.
COPY --from=build /app/target/BigGAN-ImageCreator-1.0-SNAPSHOT.jar /BigGAN-ImageCreator-1.0-SNAPSHOT.jar

# Run the web service on container startup.
CMD ["java", "-jar", "/BigGAN-ImageCreator-1.0-SNAPSHOT.jar"]