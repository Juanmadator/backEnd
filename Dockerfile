FROM adoptopenjdk/openjdk11:alpine

COPY target/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar /app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "/app/socialFitnessBackEnd-0.0.1-SNAPSHOT.jar"]