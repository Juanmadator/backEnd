# Stage 1: Maven build
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /workspace/app

# Copiar el archivo pom.xml y resolver las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente del proyecto y construir
COPY src ./src

# Ejecutar la construcción de Maven
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /workspace/app/target/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar /app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar

# Exponer el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "socialFitnessBackEnd-0.0.1-SNAPSHOT.jar"]
