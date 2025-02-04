package dev.reloadx.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface SubCommand {
    String getName();
    String getDescription();
    String getUsage();
    String getPermission();
    boolean execute(CommandSender sender, Command command, String label, String[] args);
}
