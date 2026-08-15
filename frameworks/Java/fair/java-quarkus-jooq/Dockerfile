FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/quarkus-app/ ./

ENV VIRTUAL_THREADS_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
