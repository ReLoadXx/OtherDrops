package dev.reloadx;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class OtherDrops extends JavaPlugin implements Listener {

    private final Random random = new Random();

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(this, this);

        saveDefaultConfig();

        getLogger().info("OtherDrops plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OtherDrops plugin has been disabled!");
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        String mobType = event.getEntity().getType().toString();

        ConfigurationSection mobConfig = getConfig().getConfigurationSection("mobs." + mobType);
        if (mobConfig != null) {
            String itemName = mobConfig.getString("item");
            int dropChance = mobConfig.getInt("chance", 0);

            Material material = Material.matchMaterial(itemName);
            if (material == null) {
                getLogger().warning("El material '" + itemName + "' no es válido en config.yml.");
                return;
            }

            int randomValue = random.nextInt(100) + 1;

            if (randomValue <= dropChance) {
                ItemStack customItem = new ItemStack(material);
                event.getDrops().add(customItem);
                getLogger().info("¡" + mobType + " ha dropeado " + itemName + " (probabilidad: " + dropChance + "%)!");
            } else {
                getLogger().info("¡" + mobType + " no dropeó nada (probabilidad: " + dropChance + "%)!");
            }
        }
    }
}
