package dev.reloadx.commands;

import dev.reloadx.OtherDrops;
import dev.reloadx.config.OtherDropsConfig;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.*;

public class OtherDropsCommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, SubCommand> subcommands = new HashMap<>();
    private final OtherDropsConfig config;

    public OtherDropsCommandManager(OtherDrops plugin, OtherDropsConfig config) {
        this.config = config;
        registerSubCommands();

        Objects.requireNonNull(plugin.getCommand("otherdrops")).setExecutor(this);
        Objects.requireNonNull(plugin.getCommand("otherdrops")).setTabCompleter(this);
    }

    private void registerSubCommands() {
        registerSubCommand(new GiveItemCommand(config));
    }

    private void registerSubCommand(SubCommand subCommand) {
        subcommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§6===== OtherDrops Comandos =====");
            for (SubCommand subCommand : subcommands.values()) {
                sender.sendMessage("§e/" + label + " " + subCommand.getName() + " §7- " + subCommand.getDescription());
            }
            return true;
        }

        SubCommand subCommand = subcommands.get(args[0].toLowerCase());

        if (subCommand == null) {
            sender.sendMessage("§cSubcomando desconocido. Usa: /otherdrops para ver la lista.");
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
            return new ArrayList<>(config.getConfig().getConfigurationSection("items").getKeys(false));
        }
        return new ArrayList<>();
    }
}
