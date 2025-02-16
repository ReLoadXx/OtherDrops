package dev.reloadx.commands.subcommands;

import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.commands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class ReloadCommand implements SubCommand {
    private final Plugin plugin;
    private final MessageUtils messageUtils;
    private final MessagesConfig messagesConfig;
    private final OtherDropsConfig otherDropsConfig;
    private final OtherFishingConfig otherFishingConfig;

    public ReloadCommand(Plugin plugin, MessageUtils messageUtils, MessagesConfig messagesConfig,
                         OtherDropsConfig otherDropsConfig, OtherFishingConfig otherFishingConfig) {
        this.plugin = plugin;
        this.messageUtils = messageUtils;
        this.messagesConfig = messagesConfig;
        this.otherDropsConfig = otherDropsConfig;
        this.otherFishingConfig = otherFishingConfig;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Recarga la configuración del plugin.";
    }

    @Override
    public String getUsage() {
        return "/othercore reload";
    }

    @Override
    public String getPermission() {
        return "otherdrops.reload";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(messageUtils.getMessage("no-permission"));
            return true;
        }

        try {
            plugin.reloadConfig();
            messagesConfig.reload();

            otherDropsConfig.reload();
            otherFishingConfig.reload();

            sender.sendMessage(messageUtils.getMessage("reload-success"));
        } catch (Exception e) {
            sender.sendMessage(messageUtils.getMessage("reload-failure"));
            e.printStackTrace();
        }
        return true;
    }
}
