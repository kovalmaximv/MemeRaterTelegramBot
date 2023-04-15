FROM openjdk:17
MAINTAINER mkskoval
ENV BOT_TOKEN=placeholder
COPY build/libs/MemeRaterTelegramBot-1.0-SNAPSHOT.jar MemeRaterBot.jar
ENTRYPOINT ["java","-jar","/MemeRaterBot.jar"]