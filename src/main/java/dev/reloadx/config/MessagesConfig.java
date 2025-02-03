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
        File file = new File(this.plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }

        this.messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        this.loadMessages();
    }

    public String getPrefix() {
        return this.messagesConfig.getString("prefix", "&8[&bOtherDrops&8] &7");
    }

    public String getMessage(String key) {
        return this.messagesConfig.getString("general." + key, "&cMensaje no encontrado: " + key);
    }
}