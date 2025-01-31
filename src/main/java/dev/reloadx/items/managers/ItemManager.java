package dev.reloadx.items.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemManager {

    public static ItemStack createCustomItem(Material material) {
        return new ItemStack(material);
    }
}
