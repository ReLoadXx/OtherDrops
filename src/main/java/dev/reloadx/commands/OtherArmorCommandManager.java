package dev.reloadx.commands;

import dev.reloadx.OtherCore;
import dev.reloadx.commands.subcommands.GiveArmorCommand;
import dev.reloadx.config.OtherArmorConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.StringUtil;

import java.util.*;

public class OtherArmorCommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subcommands = new HashMap<>();
    private final OtherArmorConfig config;
    private final MessageUtils messageUtils;

    public OtherArmorCommandManager(OtherCore plugin, OtherArmorConfig config, MessageUtils messageUtils) {
        this.config = config;
        this.messageUtils = messageUtils;
        registerSubCommands();

        Objects.requireNonNull(plugin.getCommand("otherarmor")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("otherarmor")).setTabCompleter(this);
    }

    private void registerSubCommands() {
        registerSubCommand(new GiveArmorCommand(config, messageUtils));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subcommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(messageUtils.getMessage("command-list-header"));
            for (SubCommand subCommand : subcommands.values()) {
                sender.sendMessage(messageUtils.getMessage("command-list-item")
                        .replace("%command%", label + " " + subCommand.getName())
                        .replace("%description%", subCommand.getDescription()));
            }
            return true;
        }

        SubCommand subCommand = subcommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            sender.sendMessage(messageUtils.getMessage("unknown-subcommand"));
            return true;
        }

        return subCommand.execute(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommandNames = new ArrayList<>(subcommands.keySet());
            return StringUtil.copyPartialMatches(args[0], subcommandNames, new ArrayList<>());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            ConfigurationSection itemsSection = config.getConfig().getConfigurationSection("sets");
            if (itemsSection != null) {
                return new ArrayList<>(itemsSection.getKeys(false));
            }
        }
        return new ArrayList<>();
    }
}
