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
import java.util.Optional;

public class MobDeathListener implements Listener {
    private final OtherDropsConfig lootConfig;
    private final DropProcessor dropProcessor;

    public MobDeathListener(OtherDropsConfig lootConfig) {
        this.lootConfig = lootConfig;
        this.dropProcessor = new DropProcessor(lootConfig.getPlugin().getLogger());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }

        Player player = event.getEntity().getKiller();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemInHand.getItemMeta();

        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();

        ConfigurationSection items = lootConfig.getConfig().getConfigurationSection("items");
        if (items == null) {
            return;
        }

        for (String itemKey : items.getKeys(false)) {
            ConfigurationSection itemData = items.getConfigurationSection(itemKey);
            if (itemData == null) continue;

            String configDisplayName = itemData.getString("display_name");
            if (configDisplayName == null) {
                continue;
            }

            String translatedDisplayName = ColorUtils.hex(configDisplayName);

            if (!displayName.equals(translatedDisplayName)) continue;

            ConfigurationSection mobs = itemData.getConfigurationSection("mobs");
            if (mobs == null) {
                continue;
            }

            EntityType mobType = event.getEntityType();
            String mobKey = mobType.toString();

            if (!mobs.contains(mobKey)) {
                continue;
            }

            List<Map<?, ?>> drops = mobs.getMapList(mobKey + ".drops");
            if (drops.isEmpty()) {
                continue;
            }

            Optional<ItemStack> drop = dropProcessor.processDrops(drops);
            drop.ifPresent(itemStack -> {
                event.getDrops().add(itemStack);
            });
        }
    }
}
