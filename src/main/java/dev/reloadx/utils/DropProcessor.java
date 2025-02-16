package dev.reloadx.utils;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.logging.Logger;

public class DropProcessor {
    private final Random random = new Random();
    private final Logger logger;
    private static boolean hasExecutableItems = false;
    private final MobSpawner mobSpawner;

    public DropProcessor(Logger logger) {
        this.logger = logger;
        this.mobSpawner = new MobSpawner(logger);
        checkExecutableItemsPresence();
    }

    private void checkExecutableItemsPresence() {
        Plugin executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
        if (executableItems != null && executableItems.isEnabled()) {
            logger.info("[DropProcessor] ¡ExecutableItems conectado!");
            hasExecutableItems = true;
        }
    }

    public Optional<Object> processDrops(List<Map<?, ?>> drops, Location location) {
        List<DropEntry> dropEntries = new ArrayList<>();
        int totalWeight = 0;

        for (Map<?, ?> dropMap : drops) {
            try {
                double chance = ((Number) dropMap.get("chance")).doubleValue();
                int weight = (int) (chance * 100);
                totalWeight += weight;

                dropEntries.add(new DropEntry(weight, dropMap));
            } catch (Exception e) {
                logger.warning("Error procesando la caída: " + e.getMessage());
            }
        }

        if (totalWeight < 10000) {
            dropEntries.add(new DropEntry(10000 - totalWeight, null));
            totalWeight = 10000;
        }

        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (DropEntry entry : dropEntries) {
            currentWeight += entry.weight;
            if (roll < currentWeight) {
                if (entry.dropData != null) {
                    ConfigurationSection config = convertMapToConfigSection(entry.dropData);

                    if (config.contains("mob")) {
                        mobSpawner.spawnMob(location, config);
                        return Optional.of(config);
                    } else if (hasExecutableItems && config.contains("display_name")) {
                        String displayName = config.getString("display_name", "");
                        if (displayName.startsWith("EI_")) {
                            String itemNameWithoutPrefix = displayName.substring(3);

                            Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI
                                    .getExecutableItemsManager()
                                    .getExecutableItem(itemNameWithoutPrefix);

                            if (eiOpt.isPresent()) {
                                ItemStack item = eiOpt.get().buildItem(1, Optional.empty(), Optional.empty());
                                return Optional.of(item);
                            }
                        }
                    }

                    ItemStack item = ItemUtils.createItem(config);

                    int quantity = config.getInt("quantity", 1);
                    item.setAmount(quantity);
                    return Optional.of(item);
                } else {
                    return Optional.empty();
                }
            }
        }

        return Optional.empty();
    }

    private ConfigurationSection convertMapToConfigSection(Map<?, ?> map) {
        YamlConfiguration config = new YamlConfiguration();
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                config.createSection(key.toString(), (Map<?, ?>) value);
            } else {
                config.set(key.toString(), value);
            }
        });
        return config;
    }

    private static class DropEntry {
        int weight;
        Map<?, ?> dropData;

        DropEntry(int weight, Map<?, ?> dropData) {
            this.weight = weight;
            this.dropData = dropData;
        }
    }
}
