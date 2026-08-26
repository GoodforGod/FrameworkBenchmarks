FROM fair-gradle-cache-jdk25:latest AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle java-micronaut-r2dbc-repository-reactive:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=64m -XX:MaxInlineLevel=20"
ENV POSTGRES_R2DBC_URL="r2dbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV VIRTUAL_THREADS_ENABLED="false"

COPY --from=builder /home/gradle/src/java-micronaut-r2dbc-repository-reactive/build/distributions/application.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app && rm /app/application.tar

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
