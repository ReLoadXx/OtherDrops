package dev.reloadx.events;

import dev.reloadx.config.ConfigManager;
import dev.reloadx.config.MobDropConfig;
import dev.reloadx.items.builders.CustomItemBuilder;
import dev.reloadx.items.models.CustomItem;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;

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
            MobDropConfig dropConfig = new MobDropConfig(mobConfig);

            if (dropConfig.isVanillaLootDisabled()) {
                event.getDrops().clear();
            }

            List<CustomItem> customItems = dropConfig.getCustomItems().stream()
                    .map(this::mapToCustomItem)
                    .collect(Collectors.toList());

            if (!customItems.isEmpty()) {
                dropItems(event, customItems);
            }
        }
    }

    private CustomItem mapToCustomItem(Map<String, Object> itemMap) {
        Material material = Material.matchMaterial((String) itemMap.get("item"));
        String customName = (String) itemMap.get("custom-name");
        List<String> lore = (List<String>) itemMap.get("lore");
        int quantity = itemMap.containsKey("quantity") ? ((Number) itemMap.get("quantity")).intValue() : 1;
        double chance = ((Number) itemMap.get("chance")).doubleValue();

        return new CustomItem(material, customName, lore, quantity, chance);
    }

    private void dropItems(EntityDeathEvent event, List<CustomItem> customItems) {
        double totalChance = customItems.stream()
                .mapToDouble(CustomItem::getChance)
                .sum();

        double emptyChance = totalChance < 100 ? 100 - totalChance : 0;
        int randomValue = random.nextInt((int) ((totalChance + emptyChance) * 100));

        double accumulatedChance = 0;
        for (CustomItem customItem : customItems) {
            accumulatedChance += customItem.getChance() * 100;

            if (randomValue < accumulatedChance) {
                ItemStack itemStack = new CustomItemBuilder(customItem.getMaterial())
                        .setName(customItem.getCustomName())
                        .setLore(customItem.getLore())
                        .build();

                itemStack.setAmount(customItem.getQuantity());
                event.getDrops().add(itemStack);
                return;
            }
        }
    }
}