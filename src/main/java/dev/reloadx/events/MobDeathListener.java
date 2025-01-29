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
import java.util.Map;
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
            boolean disableVanillaLoot = mobConfig.getBoolean("disable-vanilla-loot", false);
            boolean onlyOneDrop = mobConfig.getBoolean("only-one-drop", false); // Leer la nueva opción

            List<Map<String, Object>> items = (List<Map<String, Object>>) mobConfig.getList("items");

            if (disableVanillaLoot) {
                event.getDrops().clear();
            }

            if (items != null && !items.isEmpty()) {
                dropItems(event, items, onlyOneDrop);
            }
        }
    }

    private void dropItems(EntityDeathEvent event, List<Map<String, Object>> items, boolean onlyOneDrop) {
        for (Map<String, Object> itemMap : items) {
            String itemName = (String) itemMap.get("item");

            Object chanceObj = itemMap.get("chance");

            double dropChanceDecimal = 0;
            if (chanceObj instanceof Double) {
                dropChanceDecimal = (Double) chanceObj;
            } else if (chanceObj instanceof Integer) {
                dropChanceDecimal = ((Integer) chanceObj).doubleValue();
            }

            int dropChance = (int) (dropChanceDecimal * 100);
            String customName = (String) itemMap.get("custom-name");
            List<String> lore = (List<String>) itemMap.get("lore");

            Material material = Material.matchMaterial(itemName);
            if (material == null) continue;

            int randomValue = random.nextInt(10000);
            boolean shouldDrop = randomValue < dropChance;

            if (shouldDrop) {
                ItemStack customItem = new CustomItemBuilder(material)
                        .setName(customName)
                        .setLore(lore)
                        .build();

                event.getDrops().add(customItem);

                if (onlyOneDrop) {
                    break;
                }
            }
        }
    }
}
