FROM gradle:9.4.0-jdk25 as builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle korad-loom-undertow:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy

WORKDIR /

COPY --from=builder /home/gradle/src/korad-loom-undertow/build/distributions/app.tar /app/application.tar
COPY --from=builder /home/gradle/src/agent/build/libs/loom-agent.jar /app/loom-agent.jar
RUN tar -xf /app/application.tar -C /app
RUN rm /app/application.tar

ENV JAVA_OPTS "-XX:+UseNUMA --enable-preview --add-exports java.base/jdk.internal.misc=ALL-UNNAMED -javaagent:/app/loom-agent.jar"
ENV POOL_MODE "CARRIER"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV KORA_CONFIG_WATCHER_ENABLED="false"

EXPOSE 8080
ENTRYPOINT ["/app/app/bin/app"]
