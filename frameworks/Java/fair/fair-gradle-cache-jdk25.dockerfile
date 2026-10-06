FROM gradle:9.8.0-jdk25

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . /home/gradle/src

RUN gradle resolveFairDependencies --no-daemon --quiet
