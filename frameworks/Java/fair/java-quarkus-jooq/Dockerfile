FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/quarkus-app/ ./

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=256m -XX:ReservedCodeCacheSize=256m -XX:MaxInlineLevel=20 -XX:-StackTraceInThrowable -XX:+UseCompactObjectHeaders -XX:+UseCompressedClassPointers"
ENV VIRTUAL_THREADS_ENABLED=true

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar quarkus-run.jar"]
