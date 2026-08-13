FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/libs/java-spring-r2dbc-client-reactive.jar app.jar

# Virtual Threads (по умолчанию выключены)
ENV VIRTUAL_THREADS_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
