package dev.reloadx.events;

import dev.reloadx.config.ConfigManager;
import dev.reloadx.items.CustomItemBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class MobDeathListener implements Listener {

    private final ConfigManager configManager;
    private final Random random = new Random();

    public MobDeathListener(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        EntityType entityType = event.getEntity().getType();
        ConfigurationSection mobConfig = configManager.getConfig().getConfigurationSection("mobs." + entityType);

        if (mobConfig != null) {
            String itemName = mobConfig.getString("item");
            int dropChance = mobConfig.getInt("chance", 0);
            boolean disableVanillaLoot = mobConfig.getBoolean("disable-vanilla-loot", false);
            String customName = mobConfig.getString("custom-name");
            List<String> lore = mobConfig.getStringList("lore");

            Material material = Material.matchMaterial(itemName);
            if (material == null) {
                return;
            }

            if (disableVanillaLoot) {
                event.getDrops().clear();
            }

            if (random.nextInt(100) + 1 <= dropChance) {
                ItemStack customItem = new CustomItemBuilder(material)
                        .setName(customName)
                        .setLore(lore)
                        .build();

                event.getDrops().add(customItem);
            }
        }
    }
}
