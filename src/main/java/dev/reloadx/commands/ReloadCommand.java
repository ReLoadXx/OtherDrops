package dev.reloadx.commands;

import dev.reloadx.config.MessagesConfig;
import dev.reloadx.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements CommandExecutor {
    private final Plugin plugin;
    private final MessageUtils messageUtils;
    private final MessagesConfig messagesConfig;

    public ReloadCommand(Plugin plugin, MessageUtils messageUtils, MessagesConfig messagesConfig) {
        this.plugin = plugin;
        this.messageUtils = messageUtils;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("otherdrops.reload")) {
            sender.sendMessage(messageUtils.getMessage("no-permission"));
            return true;
        }

        try {
            plugin.reloadConfig();
            messagesConfig.reload();
            sender.sendMessage(messageUtils.getMessage("reload-success"));
        } catch (Exception e) {
            sender.sendMessage(messageUtils.getMessage("reload-failure"));
        }
        return true;
    }
}
