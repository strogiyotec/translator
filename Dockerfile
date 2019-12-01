FROM openjdk:8
MAINTAINER Almas Abdrazak <almas337519@gmail.com>
COPY /target/translator.jar /usr/src/app/
WORKDIR /usr/src/app
EXPOSE 8080
CMD java -XX:+PrintFlagsFinal -jar translator.jar