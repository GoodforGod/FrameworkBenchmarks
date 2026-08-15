FROM gradle:9.5.1-jdk25 AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle java-kora1-jdbc-repository:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

# Virtual Threads (через JVM flag)
# -Djdk.virtualThreadScheduler.parallelism=256
ENV DEFAULT_JVM_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:MaxInlineLevel=20 -XX:-StackTraceInThrowable -XX:+UseCompactObjectHeaders -XX:+UseCompressedClassPointers"

ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV KORA_CONFIG_WATCHER_ENABLED="false"
ENV VIRTUAL_THREADS_ENABLED="false"

COPY --from=builder /home/gradle/src/java-kora1-jdbc-repository/build/distributions/application.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app
RUN rm /app/application.tar

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
