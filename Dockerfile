# Stage 1: Maven build
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn -B  -e -X -C clean package

# Stage 2: Run
FROM openjdk:17
COPY --from=build /workspace/app/target/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar /app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar"]

