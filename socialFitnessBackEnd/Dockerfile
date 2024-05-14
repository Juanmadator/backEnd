# Usa una imagen de Java como base
FROM openjdk:11-jre-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR construido de tu aplicación Spring Boot al directorio de trabajo en el contenedor
COPY target/socialFitnessBackEnd.jar app.jar

# Expone el puerto en el que tu aplicación Spring Boot escucha las solicitudes
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot cuando el contenedor se inicie
CMD ["java", "-jar", "app.jar"]
