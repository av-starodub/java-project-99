FROM node:20.6.1 AS frontend

WORKDIR /frontend

RUN npm i @hexlet/java-task-manager-frontend

RUN npx build-frontend

FROM gradle:8.11.1-jdk21 AS build

WORKDIR /backend

COPY config config
COPY gradle gradle
COPY gradlew .
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN ./gradlew --no-daemon dependencies

COPY src src

COPY src/main/resources/certs/private.pem /backend/src/main/resources/certs/
COPY src/main/resources/certs/public.pem /backend/src/main/resources/certs/

COPY --from=frontend /frontend/src/main/resources/static /backend/src/main/resources/static

RUN ./gradlew --no-daemon build

FROM eclipse-temurin:21-jdk

WORKDIR /backend

RUN apt-get update && apt-get install -y sudo

COPY --from=build /backend/build/libs/app-0.0.1-SNAPSHOT.jar .

RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser

ENV JAVA_OPTS "-Xmx512M -Xms512M -XX:+UseG1GC"

CMD ["sh", "-c", "java $JAVA_OPTS -jar app-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod"]
#CMD ["sh", "-c", "java $JAVA_OPTS -jar app-0.0.1-SNAPSHOT.jar"]
