package dev.reloadx.items;

import dev.reloadx.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class CustomItemBuilder {

    private final ItemStack itemStack;

    public CustomItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public CustomItemBuilder setName(String name) {
        if (name != null && !name.isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ColorUtils.hex(name));
                itemStack.setItemMeta(meta);
            }
        }
        return this;
    }

    public CustomItemBuilder setLore(List<String> lore) {
        if (lore != null && !lore.isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                List<String> coloredLore = lore.stream()
                        .map(ColorUtils::hex)
                        .collect(Collectors.toList());
                meta.setLore(coloredLore);
                itemStack.setItemMeta(meta);
            }
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}

