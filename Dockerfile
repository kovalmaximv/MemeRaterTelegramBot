FROM openjdk:17
MAINTAINER mkskoval
ENV BOT_TOKEN=placeholder
ENV DB_USERNAME=placeholder
ENV DB_PASSWORD=placeholder
ENV DB_URL "jdbc:postgresql://host.docker.internal:5432/meme_rates_db"
COPY build/libs/MemeRaterTelegramBot-1.0-SNAPSHOT.jar MemeRaterBot.jar
ENTRYPOINT ["java","-jar","/MemeRaterBot.jar"]