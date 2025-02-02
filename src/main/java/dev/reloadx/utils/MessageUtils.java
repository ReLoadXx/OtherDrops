package dev.reloadx.utils;

import dev.reloadx.config.MessagesConfig;

public class MessageUtils {
    private final MessagesConfig messagesConfig;

    public MessageUtils(MessagesConfig messagesConfig) {
        this.messagesConfig = messagesConfig;
    }

    public String getMessage(String key) {
        String prefix = messagesConfig.getPrefix();
        String message = messagesConfig.getMessage(key);
        return ColorUtils.hex(prefix + message);
    }
}
