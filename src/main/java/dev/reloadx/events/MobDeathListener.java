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

            List<Map<String, Object>> items = (List<Map<String, Object>>) mobConfig.getList("items");

            if (disableVanillaLoot) {
                event.getDrops().clear();
            }

            if (items != null && !items.isEmpty()) {
                dropItems(event, items);
            }
        }
    }

    private void dropItems(EntityDeathEvent event, List<Map<String, Object>> items) {
        double totalChance = items.stream()
                .mapToDouble(item -> ((Number) item.get("chance")).doubleValue())
                .sum();

        double emptyChance = totalChance < 100 ? 100 - totalChance : 0;

        int randomValue = random.nextInt((int) ((totalChance + emptyChance) * 100));

        double accumulatedChance = 0;
        for (Map<String, Object> itemMap : items) {
            double chance = ((Number) itemMap.get("chance")).doubleValue() * 100;
            accumulatedChance += chance;

            if (randomValue < accumulatedChance) {
                String itemName = (String) itemMap.get("item");
                String customName = (String) itemMap.get("custom-name");
                List<String> lore = (List<String>) itemMap.get("lore");

                int quantity = itemMap.containsKey("quantity")
                        ? ((Number) itemMap.get("quantity")).intValue()
                        : 1;

                Material material = Material.matchMaterial(itemName);
                if (material == null) continue;

                ItemStack customItem = new CustomItemBuilder(material)
                        .setName(customName)
                        .setLore(lore)
                        .build();

                customItem.setAmount(quantity);

                event.getDrops().add(customItem);
                return;
            }
        }
    }
}
