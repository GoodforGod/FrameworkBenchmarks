FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/libs/java-quarkus-jpa-dao.jar app.jar

ENV VIRTUAL_THREADS_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
