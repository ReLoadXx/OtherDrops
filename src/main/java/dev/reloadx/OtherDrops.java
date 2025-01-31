package dev.reloadx;

import dev.reloadx.config.ConfigManager;
import dev.reloadx.events.MobDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherDrops extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        Bukkit.getPluginManager().registerEvents(new MobDeathListener(configManager), this);
        getLogger().info("OtherDrops plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OtherDrops plugin has been disabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}

