FROM maven:3.9.14-eclipse-temurin-25 as maven
WORKDIR /undertow
COPY pom.xml .
COPY src src
RUN mvn package -B -q -Dmaven.wagon.http.readTimeout=60000 -Dmaven.wagon.http.connectionTimeout=60000

FROM eclipse-temurin:25-jre-jammy AS runner
WORKDIR /undertow
COPY --from=maven /undertow/target/lib lib
COPY --from=maven /undertow/target/app.jar .

EXPOSE 8080

CMD ["java", "-jar", "app.jar", "POSTGRESQL"]
