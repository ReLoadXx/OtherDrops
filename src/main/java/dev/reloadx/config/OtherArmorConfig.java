package dev.reloadx.config;

import dev.reloadx.OtherCore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OtherArmorConfig {
    private final OtherCore plugin;
    private File configFile;
    private FileConfiguration config;

    public OtherArmorConfig(OtherCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public OtherCore getPlugin() {
        return plugin;
    }

    private void loadConfig() {
        configFile = new File(plugin.getDataFolder(), "otherarmors.yml");
        if (!configFile.exists()) {
            plugin.saveResource("otherarmors.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public ConfigurationSection getSets() {
        ConfigurationSection sets = config.getConfigurationSection("sets");
        if (sets == null) {
            plugin.getLogger().warning("[OtherArmor] La sección 'sets' no existe en otherarmor.yml!");
        }
        return sets;
    }

    public void saveConfig() {
        if (configFile == null || config == null) {
            plugin.getLogger().severe("[OtherArmor] Error: Configuración no cargada, no se puede guardar.");
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("[OtherArmor] No se pudo guardar otherarmor.yml!");
            e.printStackTrace();
        }
    }
}
