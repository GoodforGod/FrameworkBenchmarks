FROM gradle:9.4.0-jdk25 AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle kora2-jdbc:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV DEFAULT_JVM_OPTS="-XX:+UseParallelGC -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:-StackTraceInThrowable"

ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV KORA_CONFIG_WATCHER_ENABLED="false"
ENV JET_FAST="true"

COPY --from=builder /home/gradle/src/kora2-jdbc/build/distributions/application.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app
RUN rm /app/application.tar

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
