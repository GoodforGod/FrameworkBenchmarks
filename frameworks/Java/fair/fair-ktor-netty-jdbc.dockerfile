FROM fair-gradle-cache-jdk25:latest AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle kotlin-ktor-netty-jdbc-driver:distTar --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:MaxInlineLevel=20 -XX:-StackTraceInThrowable -XX:+UseCompactObjectHeaders -XX:+UseCompressedClassPointers"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"

COPY --from=builder /home/gradle/src/kotlin-ktor-netty-jdbc-driver/build/distributions/application.tar /app/application.tar
RUN tar -xf /app/application.tar -C /app && rm /app/application.tar

EXPOSE 8080

ENTRYPOINT ["/app/application/bin/application"]
