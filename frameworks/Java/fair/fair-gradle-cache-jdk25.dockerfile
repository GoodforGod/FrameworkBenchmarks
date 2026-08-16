FROM gradle:9.5.1-jdk25

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . /home/gradle/src

RUN gradle resolveFairDependencies --no-daemon --quiet
