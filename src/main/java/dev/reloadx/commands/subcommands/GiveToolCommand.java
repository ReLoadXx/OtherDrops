package dev.reloadx.commands.subcommands;

import dev.reloadx.commands.SubCommand;
import dev.reloadx.config.OtherBlocksConfig;
import dev.reloadx.utils.ItemUtils;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveToolCommand implements SubCommand {
    private final OtherBlocksConfig blocksConfig;
    private final MessageUtils messageUtils;

    public GiveToolCommand(OtherBlocksConfig blocksConfig, MessageUtils messageUtils) {
        this.blocksConfig = blocksConfig;
        this.messageUtils = messageUtils;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Da una herramienta especial a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/otherblocks give <herramienta> [jugador]";
    }

    @Override
    public String getPermission() {
        return "otherblocks.give";
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

        String toolKey = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(messageUtils.getMessage("player-not-found"));
            return true;
        }

        ConfigurationSection toolConfig = blocksConfig.getConfig().getConfigurationSection("special_tools." + toolKey);
        if (toolConfig == null) {
            sender.sendMessage(messageUtils.getMessage("item-not-found").replace("%item%", toolKey));
            return true;
        }

        ItemStack tool = ItemUtils.createItem(toolConfig);
        if (tool == null) {
            sender.sendMessage(messageUtils.getMessage("failed-item-creation").replace("%item%", toolKey));
            return true;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), tool);
            sender.sendMessage(messageUtils.getMessage("give-full-inventory").replace("%player%", target.getName()));
        } else {
            target.getInventory().addItem(tool);
            sender.sendMessage(messageUtils.getMessage("give-success")
                    .replace("%item%", tool.getItemMeta().getDisplayName())
                    .replace("%player%", target.getName()));
        }

        return true;
    }
}

