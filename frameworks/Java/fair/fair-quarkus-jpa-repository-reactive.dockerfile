FROM fair-gradle-cache-jdk25:latest AS builder

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle java-quarkus-jpa-repository-reactive:quarkusBuild --no-daemon

FROM eclipse-temurin:25-jre-jammy AS runner

WORKDIR /app

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:MaxInlineLevel=20 -XX:-StackTraceInThrowable -XX:+UseCompactObjectHeaders -XX:+UseCompressedClassPointers"
ENV POSTGRES_HOST="tfb-database"
ENV POSTGRES_PORT="5432"
ENV POSTGRES_DATABASE="hello_world"
ENV POSTGRES_JDBC_URL="jdbc:postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_REACTIVE_URL="postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_R2DBC_URL="postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_VERTX_URI="postgresql://tfb-database:5432/hello_world"
ENV POSTGRES_USER="benchmarkdbuser"
ENV POSTGRES_PASS="benchmarkdbpass"
ENV POSTGRES_PASSWORD="benchmarkdbpass"
ENV VIRTUAL_THREADS_ENABLED="true"

COPY --from=builder /home/gradle/src/java-quarkus-jpa-repository-reactive/build/quarkus-app/ ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]
