package dev.reloadx.items.models;

import org.bukkit.Material;
import java.util.List;

public class CustomItem {

    private Material material;
    private String customName;
    private List<String> lore;
    private int quantity;
    private double chance;

    // Constructor
    public CustomItem(Material material, String customName, List<String> lore, int quantity, double chance) {
        this.material = material;
        this.customName = customName;
        this.lore = lore;
        this.quantity = quantity;
        this.chance = chance;
    }

    // Getters
    public Material getMaterial() {
        return material;
    }

    public String getCustomName() {
        return customName;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getChance() {
        return chance;
    }
}
