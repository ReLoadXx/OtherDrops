package dev.reloadx.config;

import org.bukkit.configuration.ConfigurationSection;
import java.util.List;
import java.util.Map;

public class MobDropConfig {

    private final ConfigurationSection mobConfig;

    public MobDropConfig(ConfigurationSection mobConfig) {
        this.mobConfig = mobConfig;
    }

    public boolean isVanillaLootDisabled() {
        return mobConfig.getBoolean("disable-vanilla-loot", false);
    }

    public List<Map<String, Object>> getCustomItems() {
        return (List<Map<String, Object>>) mobConfig.getList("items");
    }
}