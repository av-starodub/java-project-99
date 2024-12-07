clean:
	./gradlew clean

build:
	./gradlew clean build

reload-classes:
	./gradlew -t classes

install:
	./gradlew installDist

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

 report:
	./gradlew jacocoTestReport

run-dist:
	./build/install/app/bin/app

build-run: build install run-dist

.PHONY: build
