package dev.reloadx.commands;

import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveItemCommand implements SubCommand {
    private final OtherDropsConfig config;

    public GiveItemCommand(OtherDropsConfig config) {
        this.config = config;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Da un ítem de OtherDrops a un jugador.";
    }

    @Override
    public String getUsage() {
        return "/otherdrops give <item> [jugador]";
    }

    @Override
    public String getPermission() {
        return "otherdrops.give";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(ChatColor.RED + "Uso correcto: " + getUsage());
            return true;
        }

        String itemKey = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "El jugador especificado no está en línea.");
            return true;
        }

        ConfigurationSection itemConfig = config.getConfig().getConfigurationSection("items." + itemKey);
        if (itemConfig == null) {
            sender.sendMessage(ChatColor.RED + "El ítem '" + itemKey + "' no existe en otherdrops.yml.");
            return true;
        }

        ItemStack item = ItemUtils.createItem(itemConfig);
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No se pudo crear el ítem '" + itemKey + "'.");
            return true;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItemNaturally(target.getLocation(), item);
            sender.sendMessage(ChatColor.YELLOW + "El inventario de " + target.getName() + " está lleno. El ítem ha sido arrojado al suelo.");
        } else {
            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "Has dado el ítem '" + item.getItemMeta().getDisplayName() + "' a " + target.getName() + ".");
        }

        return true;
    }

}

