package dev.reloadx.commands;

import dev.reloadx.config.OtherDropsConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
        return "";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("otherdrops.give")) {
            sender.sendMessage(ChatColor.RED + "No tienes permiso para usar este comando.");
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            sender.sendMessage(ChatColor.RED + "Uso: /otherdrops give <item> [jugador]");
            return true;
        }

        String itemKey = args[1].toLowerCase();
        Player target = args.length == 3 ? Bukkit.getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "El jugador especificado no está en línea.");
            return true;
        }

        if (!config.getConfig().contains("items." + itemKey)) {
            sender.sendMessage(ChatColor.RED + "El ítem '" + itemKey + "' no existe en otherdrops.yml.");
            return true;
        }

        String materialName = config.getConfig().getString("items." + itemKey + ".item");
        String displayName = ChatColor.translateAlternateColorCodes('&', config.getConfig().getString("items." + itemKey + ".display_name"));
        List<String> lore = config.getConfig().getStringList("items." + itemKey + ".lore");

        Material material = Material.matchMaterial(materialName);
        if (material == null) {
            sender.sendMessage(ChatColor.RED + "El material '" + materialName + "' del ítem '" + itemKey + "' no es válido.");
            return true;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Has dado el ítem '" + displayName + "' a " + target.getName() + ".");
        return true;
    }
}
