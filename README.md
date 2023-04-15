# Telegram Bot spring template
This is template that you can use for your next telegram bot. Template built on Spring Boot and has minimal 
realization of necessary telegram bot API. It remains only add your business logic. 

## Structure of the project
```
├── gradle
├── src
│   ├── main.java.ru.mkskoval
│       ├── configuration
│           ├── TelegramBotStarter.java    <- 1
│           └── TelegramConfiguration.java <- 2
│       ├── properties
│           └── BotConfiguration.java  . . <- 3
│       ├── Bot.java . . . . . . . . . . . <- 4
│       └── Main.java
│   ├── main.resources
│       ├── application.properties . . . . <- 5
│       └── log4j2.xml
├── build.gradle
├── Dockerfile . . . . . . . . . . . . . . <- 6
└── README.md
```

1) Listener for event `ApplicationReadyEvent` that register telegram's bot.
2) Configuration for necessary telegram's API beans.
3) ConfigurationProperties for telegram's bot token and your properties too.
4) Main class for telegram's bot that keeps logic of request processing.
5) Application properties keeps bot token.
6) Dockerfile for build your application to docker image.

## What should you do
1) Save token to environment variable BOT_TOKEN. For local development only you can write token in 
application.properties.
2) Realize your request processing logic in `Bot.java`
3) Realize rest of your business logic in way you want.
4) That's all!