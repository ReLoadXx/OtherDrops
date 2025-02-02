package dev.reloadx.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class MessagesConfig {
    private final Plugin plugin;
    private FileConfiguration messagesConfig;

    public MessagesConfig(Plugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    public void loadMessages() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        loadMessages();
    }

    public String getPrefix() {
        return messagesConfig.getString("prefix", "&8[&bOtherDrops&8] &7");
    }

    public String getMessage(String key) {
        return messagesConfig.getString("general." + key, "&cMensaje no encontrado: " + key);
    }
}