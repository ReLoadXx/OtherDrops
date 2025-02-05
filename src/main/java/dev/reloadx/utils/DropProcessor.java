package dev.reloadx.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.logging.Logger;

public class DropProcessor {
    private final Random random = new Random();
    private final Logger logger;

    public DropProcessor(Logger logger) {
        this.logger = logger;
    }

    public Optional<ItemStack> processDrops(List<Map<?, ?>> drops) {
        List<DropEntry> dropEntries = new ArrayList<>();
        int totalWeight = 0;

        for (Map<?, ?> dropMap : drops) {
            try {
                double chance = ((Number) dropMap.get("chance")).doubleValue();
                int weight = (int) (chance * 100);
                totalWeight += weight;

                dropEntries.add(new DropEntry(weight, dropMap));
            } catch (Exception e) {
                //LOG DEL FUTURO
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
                    ConfigurationSection itemConfig = convertMapToConfigSection(entry.dropData);
                    ItemStack item = ItemUtils.createItem(itemConfig);

                    int quantity = 1;
                    if (entry.dropData.containsKey("quantity")) {
                        quantity = ((Number) entry.dropData.get("quantity")).intValue();
                    }

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
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
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
