FROM gradle:9.5.1-jdk21

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . /home/gradle/src

RUN cp fair-gradle-cache-jdk21.settings.gradle settings.gradle \
    && gradle resolveFairDependencies --no-daemon --quiet
