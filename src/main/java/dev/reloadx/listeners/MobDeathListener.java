package dev.reloadx.listeners;

import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.utils.DropProcessor;
import dev.reloadx.utils.ColorUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class MobDeathListener implements Listener {
    private final OtherDropsConfig lootConfig;
    private final DropProcessor dropProcessor;

    public MobDeathListener(OtherDropsConfig lootConfig) {
        this.lootConfig = lootConfig;
        this.dropProcessor = new DropProcessor(lootConfig.getPlugin().getLogger());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) {
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemInHand.getItemMeta();
        String displayName = (meta != null && meta.hasDisplayName()) ? meta.getDisplayName() : null;

        ConfigurationSection items = lootConfig.getConfig().getConfigurationSection("items");
        if (items == null) {
            return;
        }

        EntityType mobType = event.getEntityType();
        String mobKey = mobType.toString();
        boolean lootApplied = false;

        for (String itemKey : items.getKeys(false)) {
            if (itemKey.equals("global_drops")) continue;

            ConfigurationSection itemData = items.getConfigurationSection(itemKey);
            if (itemData == null) continue;

            String configDisplayName = itemData.getString("display_name");
            if (configDisplayName == null) continue;

            String translatedDisplayName = ColorUtils.hex(configDisplayName);
            if (displayName == null || !displayName.equals(translatedDisplayName)) continue;

            ConfigurationSection mobs = itemData.getConfigurationSection("mobs");
            if (mobs == null || !mobs.contains(mobKey)) continue;

            List<Map<?, ?>> drops = mobs.getMapList(mobKey + ".drops");
            if (!drops.isEmpty()) {
                dropProcessor.processDrops(drops, event.getEntity().getLocation()).ifPresent(drop -> {
                    if (drop instanceof ItemStack) {
                        event.getDrops().add((ItemStack) drop);
                    }
                });
                lootApplied = true;
            }
        }

        if (!lootApplied) {
            ConfigurationSection globalDrops = items.getConfigurationSection("global_drops");
            if (globalDrops != null) {
                ConfigurationSection mobs = globalDrops.getConfigurationSection("mobs");
                if (mobs != null && mobs.contains(mobKey)) {
                    List<Map<?, ?>> drops = mobs.getMapList(mobKey + ".drops");
                    if (!drops.isEmpty()) {
                        dropProcessor.processDrops(drops, event.getEntity().getLocation()).ifPresent(drop -> {
                            if (drop instanceof ItemStack) {
                                event.getDrops().add((ItemStack) drop);
                            }
                        });
                    }
                }
            }
        }
    }
}
