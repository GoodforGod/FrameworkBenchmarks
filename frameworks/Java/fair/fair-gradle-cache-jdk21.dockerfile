FROM gradle:9.8.0-jdk21

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . /home/gradle/src

RUN cp fair-gradle-cache-jdk21.settings.gradle settings.gradle \
    && gradle resolveFairDependencies --no-daemon --quiet
