package dev.reloadx.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class CustomLootConfig {
    private final Plugin plugin;
    private FileConfiguration config;

    public CustomLootConfig(Plugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public ConfigurationSection getItems() {
        return config.getConfigurationSection("items");
    }
}
