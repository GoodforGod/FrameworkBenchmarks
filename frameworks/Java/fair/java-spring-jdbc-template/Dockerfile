FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/libs/java-spring-jdbc-template.jar app.jar

ENV JAVA_OPTS="-XX:+UseParallelGC -XX:+UseNUMA -Djava.net.preferIPv4Stack=true -Djdk.trackAllThreads=false -XX:AutoBoxCacheMax=11000 -XX:InitialCodeCacheSize=64m -XX:ReservedCodeCacheSize=64m -XX:MaxInlineLevel=20"
# Virtual Threads (enabled by default)
ENV VIRTUAL_THREADS_ENABLED=true

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
