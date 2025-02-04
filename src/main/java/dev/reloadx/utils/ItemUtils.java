package dev.reloadx.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {

    /**
     * Convierte la configuración de un ítem en un ItemStack con colores aplicados.
     * @param itemConfig Sección del item en el archivo YML.
     * @return ItemStack listo para ser entregado.
     */
    public static ItemStack createItem(ConfigurationSection itemConfig) {
        if (itemConfig == null) return null;

        String itemType = itemConfig.getString("item", "STONE").toUpperCase();
        ItemStack item = new ItemStack(org.bukkit.Material.valueOf(itemType));

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (itemConfig.contains("display_name")) {
                meta.setDisplayName(ColorUtils.hex(itemConfig.getString("display_name")));
            }
            if (itemConfig.contains("lore")) {
                List<String> lore = itemConfig.getStringList("lore").stream()
                        .map(ColorUtils::hex)
                        .collect(Collectors.toList());
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}

