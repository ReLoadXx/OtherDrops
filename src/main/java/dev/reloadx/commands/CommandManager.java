package dev.reloadx.commands;

import dev.reloadx.OtherDrops;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CommandManager {
    private final Map<String, CommandExecutor> subcommands = new HashMap<>();
    private final Map<String, TabCompleter> tabCompleters = new HashMap<>();

    public CommandManager(OtherDrops plugin, MessageUtils messageUtils, MessagesConfig messagesConfig) {
        PluginCommand command = plugin.getCommand("othercore");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (args.length == 0 || !subcommands.containsKey(args[0].toLowerCase())) {
                    sender.sendMessage(messageUtils.getMessage("usage"));
                    return true;
                }

                CommandExecutor executor = subcommands.get(args[0].toLowerCase());
                return executor.onCommand(sender, cmd, label, args);
            });

            //TabCompleter
            command.setTabCompleter((sender, cmd, label, args) -> {
                if (args.length == 1) {
                    List<String> subcommandNames = new ArrayList<>(subcommands.keySet());
                    return StringUtil.copyPartialMatches(args[0], subcommandNames, new ArrayList<>());
                }

                //if (args.length == 2 && args[0].equalsIgnoreCase("reload")) {
                //    return List.of("option1", "option2");
                // }

                return new ArrayList<>();
            });

            subcommands.put("reload", new ReloadCommand(plugin, messageUtils, messagesConfig));
            // subcommands.put("otro_comando", new OtroComandoExecutor());
        }
    }
}


