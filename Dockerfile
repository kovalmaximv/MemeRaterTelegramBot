FROM openjdk:1
MAINTAINER mkskoval
ENV HIBERNATE_DB_URL "jdbc:postgresql://host.docker.internal:5432/meme_rates_db"
COPY build/libs/MemeRaterBot.jar MemeRaterBot.jar
ENTRYPOINT ["java","-jar","/MemeRaterBot.jar"]