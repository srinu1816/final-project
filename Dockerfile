FROM amazoncorretto:17
VOLUME /tmp
VOLUME /app/logs
ADD target/flightservices-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
