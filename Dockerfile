FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/Distributed-Transaction-Service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8114

ENTRYPOINT ["java", "-jar", "app.jar"]

