FROM gradle:9.4.0-jdk25 as builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle korad-blocking:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy

WORKDIR /

COPY --from=builder /home/gradle/src/korad-blocking/build/distributions/app.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app
RUN rm /app/application.tar

ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV KORA_CONFIG_WATCHER_ENABLED="false"

EXPOSE 8080
ENTRYPOINT ["/app/app/bin/app"]
