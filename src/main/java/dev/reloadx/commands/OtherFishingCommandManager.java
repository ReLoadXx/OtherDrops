package dev.reloadx.commands;

import dev.reloadx.OtherCore;
import dev.reloadx.commands.subcommands.GiveFishingRodCommand;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OtherFishingCommandManager implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subcommands = new HashMap<>();
    private final OtherFishingConfig config;
    private final MessageUtils messageUtils;

    public OtherFishingCommandManager(OtherCore plugin, OtherFishingConfig config, MessageUtils messageUtils) {
        this.config = config;
        this.messageUtils = messageUtils;
        registerSubCommands();

        Objects.requireNonNull(plugin.getCommand("otherfishing")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("otherfishing")).setTabCompleter(this);
    }

    private void registerSubCommands() {
        registerSubCommand(new GiveFishingRodCommand(config, messageUtils));
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
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            ConfigurationSection rodsSection = config.getConfig().getConfigurationSection("special_fishing_rods");
            if (rodsSection != null) {
                return new ArrayList<>(rodsSection.getKeys(false));
            }
        }
        return new ArrayList<>();
    }
}
