package dev.reloadx.config;

import dev.reloadx.OtherCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OtherFishingConfig {
    private final OtherCore plugin;
    private File configFile;
    private FileConfiguration config;

    public OtherFishingConfig(OtherCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "otherfishing.yml");
        if (!configFile.exists()) {
            plugin.saveResource("otherfishing.yml", false);
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
            plugin.getLogger().severe("[OtherFishing] No se pudo guardar otherfishing.yml!");
            e.printStackTrace();
        }
    }
}
