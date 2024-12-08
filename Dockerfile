FROM gradle:8.11.1-jdk21 AS build

WORKDIR /backend

COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN ./gradlew --no-daemon dependencies

COPY src src

RUN ./gradlew --no-daemon build

FROM openjdk:21-jdk

WORKDIR /backend

COPY --from=build /backend/build/libs/app-0.0.1-SNAPSHOT.jar .

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

ENV JAVA_OPTS "-Xmx512M -Xms512M -XX:+UseG1GC"

CMD ["sh", "-c", "java $JAVA_OPTS -jar app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]
