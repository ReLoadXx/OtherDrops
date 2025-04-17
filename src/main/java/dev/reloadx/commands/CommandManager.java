package dev.reloadx.commands;

import dev.reloadx.OtherCore;
import dev.reloadx.commands.subcommands.ReloadCommand;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.config.OtherBlocksConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subcommands = new HashMap<>();

    public CommandManager(OtherCore plugin, MessageUtils messageUtils, MessagesConfig messagesConfig,
                          OtherDropsConfig otherDropsConfig, OtherFishingConfig otherFishingConfig, OtherBlocksConfig otherBlocksConfig) {
        registerSubCommand(new ReloadCommand(plugin, messageUtils, messagesConfig, otherDropsConfig, otherFishingConfig, otherBlocksConfig));

        Objects.requireNonNull(plugin.getCommand("othercore")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("othercore")).setTabCompleter(this);
    }

    private void registerSubCommand(SubCommand subCommand) {
        subcommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUso: /othercore <subcomando>");
            return true;
        }

        SubCommand subCommand = subcommands.get(args[0].toLowerCase());

        if (subCommand == null) {
            sender.sendMessage("§cSubcomando desconocido. Usa: /othercore <subcomando>");
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
        return new ArrayList<>();
    }
}
