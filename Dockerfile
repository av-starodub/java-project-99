FROM gradle:8.11.1-jdk21

WORKDIR /

COPY / .

RUN ./gradlew installDist

CMD ./build/install/app/bin/app
