package dev.reloadx.commands.subcommands;

import dev.reloadx.commands.SubCommand;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.utils.ItemUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemCommand implements SubCommand {
    private final OtherDropsConfig config;
    private final MessageUtils messageUtils;

    public GiveItemCommand(OtherDropsConfig config, MessageUtils messageUtils) {
        this.config = config;
        this.messageUtils = messageUtils;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Da un ítem de OtherDrops a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/otherdrops give <item> [jugador]";
    }

    @Override
    public String getPermission() {
        return "otherdrops.give";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(messageUtils.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(messageUtils.getMessage("invalid-usage"));
            return true;
        }

        String itemKey = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(messageUtils.getMessage("player-not-found"));
            return true;
        }

        ConfigurationSection itemConfig = config.getConfig().getConfigurationSection("items." + itemKey);
        if (itemConfig == null) {
            sender.sendMessage(messageUtils.getMessage("item-not-found").replace("%item%", itemKey));
            return true;
        }

        ItemStack item = ItemUtils.createItem(itemConfig);
        if (item == null) {
            sender.sendMessage(messageUtils.getMessage("failed-item-creation").replace("%item%", itemKey));
            return true;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), item);
            sender.sendMessage(messageUtils.getMessage("give-full-inventory").replace("%player%", target.getName()));
        } else {
            target.getInventory().addItem(item);
            sender.sendMessage(messageUtils.getMessage("give-success")
                    .replace("%item%", item.getItemMeta().getDisplayName())
                    .replace("%player%", target.getName()));
        }

        return true;
    }
}