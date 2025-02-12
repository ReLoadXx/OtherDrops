package dev.reloadx.config;

import dev.reloadx.OtherCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OtherDropsConfig {
    private final OtherCore plugin;
    private File configFile;
    private FileConfiguration config;

    public OtherDropsConfig(OtherCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public OtherCore getPlugin() {
        return plugin;
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "otherdrops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("otherdrops.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public ConfigurationSection getItems() {
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            plugin.getLogger().warning("[OtherDrops] La sección 'items' no existe en otherdrops.yml!");
        }
        return items;
    }

    public void saveConfig() {
        if (configFile == null || config == null) {
            plugin.getLogger().severe("[OtherDrops] Error: Configuración no cargada, no se puede guardar.");
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("[OtherDrops] No se pudo guardar otherdrops.yml!");
            e.printStackTrace();
        }
    }
}
