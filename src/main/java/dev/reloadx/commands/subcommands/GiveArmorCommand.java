package dev.reloadx.commands.subcommands;

import dev.reloadx.commands.SubCommand;
import dev.reloadx.config.OtherArmorConfig;
import dev.reloadx.utils.ItemUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveArmorCommand implements SubCommand {

    private final OtherArmorConfig armorConfig;
    private final MessageUtils messageUtils;

    public GiveArmorCommand(OtherArmorConfig armorConfig, MessageUtils messageUtils) {
        this.armorConfig = armorConfig;
        this.messageUtils = messageUtils;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Entrega un set completo de armadura de OtherArmor a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/otherarmor give <set> [jugador]";
    }

    @Override
    public String getPermission() {
        return "otherarmor.give";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(messageUtils.getMessage("no-permission"));
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(messageUtils.getMessage("invalid-usage").replace("%usage%", getUsage()));
            return true;
        }

        String setName = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(messageUtils.getMessage("player-not-found"));
            return true;
        }

        ConfigurationSection setConfig = armorConfig.getSets().getConfigurationSection(setName);
        if (setConfig == null) {
            sender.sendMessage(messageUtils.getMessage("set-not-found").replace("%set%", setName));
            return true;
        }

        ItemStack helmet = ItemUtils.createItem(setConfig.getConfigurationSection("helmet"));
        ItemStack chestplate = ItemUtils.createItem(setConfig.getConfigurationSection("chestplate"));
        ItemStack leggings = ItemUtils.createItem(setConfig.getConfigurationSection("leggings"));
        ItemStack boots = ItemUtils.createItem(setConfig.getConfigurationSection("boots"));

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), helmet);
            target.getWorld().dropItemNaturally(target.getLocation(), chestplate);
            target.getWorld().dropItemNaturally(target.getLocation(), leggings);
            target.getWorld().dropItemNaturally(target.getLocation(), boots);
            sender.sendMessage(messageUtils.getMessage("give-full-inventory").replace("%player%", target.getName()));
        } else {
            target.getInventory().addItem(helmet, chestplate, leggings, boots);
            sender.sendMessage(messageUtils.getMessage("give-success")
                    .replace("%set%", setName)
                    .replace("%player%", target.getName()));
        }

        return true;
    }
}
