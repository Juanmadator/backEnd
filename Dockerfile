# Stage 1: Maven build
FROM maven:3.8.3-openjdk-21 AS build
WORKDIR /workspace/app

# Copiar el archivo pom.xml y resolver las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente del proyecto y construir
COPY src ./src

# Ejecutar la construcción de Maven
RUN mvn clean package

# Stage 2: Run
FROM openjdk:21
COPY --from=build /workspace/app/target/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar /app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar
CMD ["java", "-jar", "/app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar"]
