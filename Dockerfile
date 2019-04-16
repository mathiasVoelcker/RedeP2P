FROM openjdk:8-jdk-alpine

COPY ./ /opt/app/

COPY filesToBeShared /opt/files/

WORKDIR opt/app

RUN ./gradlew clean build

WORKDIR build/libs


