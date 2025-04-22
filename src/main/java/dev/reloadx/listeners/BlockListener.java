package dev.reloadx.listeners;

import dev.reloadx.OtherCore;
import dev.reloadx.config.OtherBlocksConfig;
import dev.reloadx.utils.ColorUtils;
import dev.reloadx.utils.DropProcessor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockListener implements Listener {
    private final OtherCore plugin;
    private final OtherBlocksConfig otherBlocksConfig;
    private final DropProcessor dropProcessor;

    public BlockListener(OtherCore plugin, OtherBlocksConfig otherBlocksConfig) {
        this.plugin = plugin;
        this.otherBlocksConfig = otherBlocksConfig;
        this.dropProcessor = new DropProcessor(plugin.getLogger());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.getBlock().setMetadata("placedByPlayer", new FixedMetadataValue(plugin, true));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        org.bukkit.block.Block block = event.getBlock();

        if (block.hasMetadata("placedByPlayer")) return;

        ConfigurationSection section = otherBlocksConfig.getConfig().getConfigurationSection("special_tools");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            Material configToolType = Material.valueOf(entry.getString("item"));
            String configToolName = ColorUtils.hex(entry.getString("display_name")).toUpperCase();

            List<String> blockList = entry.getStringList("blocks");

            if (!blockList.contains(block.getType().toString())) continue;

            if (tool == null || !tool.hasItemMeta() || !tool.getType().equals(configToolType)) continue;

            String toolDisplayName = ColorUtils.hex(tool.getItemMeta().getDisplayName()).toUpperCase();
            if (!toolDisplayName.equals(configToolName)) continue;

            List<Map<?, ?>> drops = entry.getMapList("drops");
            Optional<Object> optionalDrop = dropProcessor.processDrops(drops, block.getLocation());

            optionalDrop.ifPresent(drop -> {
                if (drop instanceof ItemStack) {
                    block.getWorld().dropItemNaturally(block.getLocation(), (ItemStack) drop);
                }
            });

            //block.setType(Material.AIR);
            break;
        }
    }
}