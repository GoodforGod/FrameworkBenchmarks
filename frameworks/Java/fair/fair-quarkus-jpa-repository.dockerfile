FROM fair-gradle-cache-jdk25:latest AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle java-quarkus-jpa-repository:quarkusBuild --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=64m -XX:MaxInlineLevel=20"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV VIRTUAL_THREADS_ENABLED="true"

COPY --from=builder /home/gradle/src/java-quarkus-jpa-repository/build/quarkus-app/ ./

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar quarkus-run.jar"]
