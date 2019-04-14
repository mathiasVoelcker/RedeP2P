FROM openjdk:8-jdk-alpine

COPY src /opt/app/

WORKDIR opt/app

RUN javac *.java


