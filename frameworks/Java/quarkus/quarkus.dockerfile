FROM maven:3.9.11-eclipse-temurin-21 as maven
ENV LANGUAGE='en_US:en'

WORKDIR /quarkus
ENV MODULE=resteasy-reactive-hibernate

COPY --chown=185 pom.xml pom.xml
COPY --chown=185 quarkus-benchmark-common quarkus-benchmark-common/
COPY --chown=185 resteasy-reactive-hibernate resteasy-reactive-hibernate/
COPY --chown=185 resteasy-reactive-hibernate-reactive resteasy-reactive-hibernate-reactive/
COPY --chown=185 vertx vertx/
COPY --chown=185 reactive-routes-pgclient reactive-routes-pgclient/

# Uncomment to test pre-release quarkus
#RUN mkdir -p /root/.m2/repository/io
#COPY m2-quarkus /root/.m2/repository/io/quarkus

WORKDIR /quarkus
RUN mvn -DskipTests install -pl :benchmark,:quarkus-benchmark-common -B -q

WORKDIR /quarkus/$MODULE
WORKDIR /quarkus

COPY $MODULE/src $MODULE/src

WORKDIR /quarkus/$MODULE
RUN mvn package -B -q
WORKDIR /quarkus

FROM eclipse-temurin:21-jre-jammy
ENV LANGUAGE='en_US:en'
WORKDIR /quarkus
ENV MODULE=resteasy-reactive-hibernate

COPY --chown=185 --from=maven /quarkus/$MODULE/target/quarkus-app/lib/ lib
COPY --chown=185 --from=maven /quarkus/$MODULE/target/quarkus-app/app/ app
COPY --chown=185 --from=maven /quarkus/$MODULE/target/quarkus-app/quarkus/ quarkus
COPY --chown=185 --from=maven /quarkus/$MODULE/target/quarkus-app/quarkus-run.jar quarkus-run.jar
COPY --chown=185 run_quarkus.sh run_quarkus.sh

EXPOSE 8080
ENTRYPOINT "./run_quarkus.sh"
