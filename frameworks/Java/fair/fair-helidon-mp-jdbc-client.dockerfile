FROM fair-gradle-cache-jdk25:latest AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle java-helidon-mp-jdbc-client:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV DEFAULT_JVM_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:MaxInlineLevel=20 -XX:-StackTraceInThrowable -XX:+UseCompactObjectHeaders -XX:+UseCompressedClassPointers"
ENV POSTGRES_HOST="tfb-database"
ENV POSTGRES_PORT="5432"
ENV POSTGRES_DATABASE="hello_world"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_R2DBC_URL="r2dbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_VERTX_URI="postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV POSTGRES_PASSWORD="benchmarkdbpass"
ENV VIRTUAL_THREADS_ENABLED="true"

COPY --from=builder /home/gradle/src/java-helidon-mp-jdbc-client/build/distributions/application.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app && rm /app/application.tar

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
