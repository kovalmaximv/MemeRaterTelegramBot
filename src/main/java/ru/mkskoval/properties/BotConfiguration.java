package ru.mkskoval.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bot")
@Data
public class BotConfiguration {
    private String token;
}
