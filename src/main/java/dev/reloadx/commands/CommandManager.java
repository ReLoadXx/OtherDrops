package dev.reloadx.commands;

import dev.reloadx.OtherDrops;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.utils.MessageUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class CommandManager {
    private final Map<String, CommandExecutor> subcommands = new HashMap();
    private final Map<String, TabCompleter> tabCompleters = new HashMap();

    public CommandManager(OtherDrops plugin, MessageUtils messageUtils, MessagesConfig messagesConfig) {
        PluginCommand command = plugin.getCommand("othercore");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (args.length != 0 && this.subcommands.containsKey(args[0].toLowerCase())) {
                    CommandExecutor executor = (CommandExecutor)this.subcommands.get(args[0].toLowerCase());
                    return executor.onCommand(sender, cmd, label, args);
                } else {
                    sender.sendMessage(messageUtils.getMessage("usage"));
                    return true;
                }
            });
            command.setTabCompleter((sender, cmd, label, args) -> {
                if (args.length == 1) {
                    List<String> subcommandNames = new ArrayList(this.subcommands.keySet());
                    return (List)StringUtil.copyPartialMatches(args[0], subcommandNames, new ArrayList());
                } else {
                    return new ArrayList();
                }
            });
            this.subcommands.put("reload", new ReloadCommand(plugin, messageUtils, messagesConfig));
        }

    }
}
