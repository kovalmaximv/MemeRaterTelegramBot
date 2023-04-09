package ru.mkskoval.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bot")
@Data
public class BotConfiguration {
    private Long memeChatId;
    private Long memeChannelId;
    private String token;
}
