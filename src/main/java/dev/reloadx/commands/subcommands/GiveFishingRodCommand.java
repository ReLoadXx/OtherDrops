package dev.reloadx.commands.subcommands;

import dev.reloadx.commands.SubCommand;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.utils.ItemUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveFishingRodCommand implements SubCommand {
    private final OtherFishingConfig config;
    private final MessageUtils messageUtils;

    public GiveFishingRodCommand(OtherFishingConfig config, MessageUtils messageUtils) {
        this.config = config;
        this.messageUtils = messageUtils;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Da una caña de pescar especial a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/otherfishing give <caña> [jugador]";
    }

    @Override
    public String getPermission() {
        return "otherfishing.give";
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

        String rodKey = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(messageUtils.getMessage("player-not-found"));
            return true;
        }

        ConfigurationSection rodConfig = config.getConfig().getConfigurationSection("special_fishing_rods." + rodKey);
        if (rodConfig == null) {
            sender.sendMessage(messageUtils.getMessage("item-not-found").replace("%item%", rodKey));
            return true;
        }

        ItemStack rod = ItemUtils.createItem(rodConfig);
        if (rod == null) {
            sender.sendMessage(messageUtils.getMessage("failed-item-creation").replace("%item%", rodKey));
            return true;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), rod);
            sender.sendMessage(messageUtils.getMessage("give-full-inventory").replace("%player%", target.getName()));
        } else {
            target.getInventory().addItem(rod);
            sender.sendMessage(messageUtils.getMessage("give-success")
                    .replace("%item%", rod.getItemMeta().getDisplayName())
                    .replace("%player%", target.getName()));
        }

        return true;
    }
}
