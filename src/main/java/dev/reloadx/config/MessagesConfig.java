package dev.reloadx.config;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MessagesConfig {
    private final Plugin plugin;
    private FileConfiguration messagesConfig;

    public MessagesConfig(Plugin plugin) {
        this.plugin = plugin;
        this.loadMessages();
    }

    public void loadMessages() {
        File messagesFile = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }
        this.messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        this.loadMessages();
    }

    public String getPrefix() {
        return this.messagesConfig.getString("prefix", "&8[&bOtherDrops&8] &7");
    }

    public String getMessage(String key) {
        String message = this.messagesConfig.getString("general." + key);
        if (message != null) {
            return message;
        }

        message = this.messagesConfig.getString("otherdrops." + key);
        if (message != null) {
            return message;
        }

        message = this.messagesConfig.getString("otherfishing." + key);
        if (message != null) {
            return message;
        }

        message = this.messagesConfig.getString("otherarmor." + key);
        if (message != null) {
            return message;
        }

        return "&cMensaje no encontrado: " + key;
    }
}
