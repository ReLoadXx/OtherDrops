package dev.reloadx.config;

import dev.reloadx.OtherDrops;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OtherDropsConfig {
    private final OtherDrops plugin;
    private File configFile;
    private FileConfiguration config;

    public OtherDropsConfig(OtherDrops plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "otherdrops.yml");
        if (!configFile.exists()) {
            plugin.saveResource("otherdrops.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("No se pudo guardar otherdrops.yml!");
        }
    }

    public void reload() {
        loadConfig();
    }
}
