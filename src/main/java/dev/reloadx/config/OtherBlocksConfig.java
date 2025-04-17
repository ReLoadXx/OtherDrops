package dev.reloadx.config;

import dev.reloadx.OtherCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OtherBlocksConfig {
    private final OtherCore plugin;
    private File configFile;
    private FileConfiguration config;

    public OtherBlocksConfig(OtherCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "otherblocks.yml");
        if (!configFile.exists()) {
            plugin.saveResource("otherblocks.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reload() {
        loadConfig();
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("[OtherBlocks] No se pudo guardar otherblocks.yml!");
            e.printStackTrace();
        }
    }
}

