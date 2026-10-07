FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/distributions/application.tar app.tar
RUN tar -xf app.tar && rm app.tar

ENV VIRTUAL_THREADS_ENABLED=false

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
