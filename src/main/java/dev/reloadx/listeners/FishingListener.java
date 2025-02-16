package dev.reloadx.listeners;

import dev.reloadx.OtherCore;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.utils.ColorUtils;
import dev.reloadx.utils.DropProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FishingListener implements Listener {
    private final OtherCore plugin;
    private final OtherFishingConfig otherFishingConfig;
    private final DropProcessor dropProcessor;

    public FishingListener(OtherCore plugin, OtherFishingConfig otherFishingConfig) {
        this.plugin = plugin;
        this.otherFishingConfig = otherFishingConfig;
        this.dropProcessor = new DropProcessor(plugin.getLogger());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == State.CAUGHT_FISH) {
            if (event.getHook().hasMetadata("dropProcessed")) {
                return;
            }
            event.getHook().setMetadata("dropProcessed", new FixedMetadataValue(plugin, true));

            Player player = event.getPlayer();
            ItemStack fishingRod = player.getInventory().getItemInMainHand();

            if (fishingRod.getType() == Material.FISHING_ROD && fishingRod.hasItemMeta()) {
                String rodName = ColorUtils.hex(fishingRod.getItemMeta().getDisplayName()).toUpperCase();
                ConfigurationSection rodsSection = otherFishingConfig.getConfig().getConfigurationSection("special_fishing_rods");

                if (rodsSection != null) {
                    for (String key : rodsSection.getKeys(false)) {
                        ConfigurationSection rodConfig = rodsSection.getConfigurationSection(key);
                        if (rodConfig != null) {
                            String configRodName = ColorUtils.hex(rodConfig.getString("display_name")).toUpperCase();

                            if (rodName.equals(configRodName)) {
                                handleFishingEvent(event, player, rodConfig);
                                event.setCancelled(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleFishingEvent(PlayerFishEvent event, Player player, ConfigurationSection rodConfig) {
        List<Map<?, ?>> drops = rodConfig.getMapList("drops");
        Optional<Object> optionalDrop = dropProcessor.processDrops(drops, player.getLocation());


        optionalDrop.ifPresent(drop -> {
            if (drop instanceof ItemStack) {
                player.getWorld().dropItemNaturally(player.getLocation(), (ItemStack) drop);
            }
        });

        event.getHook().remove();
    }
}
