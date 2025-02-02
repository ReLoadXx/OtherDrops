package dev.reloadx.commands;

import dev.reloadx.OtherDrops;
import dev.reloadx.config.CustomLootConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GiveItemCommand implements CommandExecutor {
    private final OtherDrops plugin;
    private final CustomLootConfig lootConfig;
    private final MessageUtils messageUtils;

    public GiveItemCommand(OtherDrops plugin, CustomLootConfig lootConfig, MessageUtils messageUtils) {
        this.plugin = plugin;
        this.lootConfig = lootConfig;
        this.messageUtils = messageUtils;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageUtils.getMessage("solo_jugadores"));
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(messageUtils.getMessage("comando_invalido"));
            return false;
        }

        String itemName = args[0].toLowerCase();
        Player player = (Player) sender;

        if (!player.hasPermission("otherdrops.giveitem")) {
            player.sendMessage(messageUtils.getMessage("no-permission"));
            return false;
        }

        if (itemName.equals("obsidian")) {
            ItemStack item = createObsidianItem();
            player.getInventory().addItem(item);
            player.sendMessage(messageUtils.getMessage("item_entregado").replace("%item%", itemName));
            return true;
        } else {
            player.sendMessage(messageUtils.getMessage("item_no_encontrado"));
            return false;
        }
    }

    private ItemStack createObsidianItem() {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("#FFF733Obsidian Fishing Rod");
            meta.setLore(List.of(
                    "&7Forged with obsidian",
                    "&7It's resistant and powerful"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}
