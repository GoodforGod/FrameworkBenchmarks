FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/libs/application.tar app.tar
RUN tar -xf app.tar && rm app.tar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application/bin/application"]
