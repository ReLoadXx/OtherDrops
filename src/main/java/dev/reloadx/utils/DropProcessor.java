package dev.reloadx.utils;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import com.ssomar.score.api.executableitems.config.ExecutableItemInterface;
import org.bukkit.Bukkit;
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

    public DropProcessor(Logger logger) {
        this.logger = logger;
        checkExecutableItemsPresence();
    }

    private void checkExecutableItemsPresence() {
        Plugin executableItems = Bukkit.getPluginManager().getPlugin("ExecutableItems");
        if (executableItems != null && executableItems.isEnabled()) {
            logger.info("[DropProcessor] ¡ExecutableItems conectado!");
            hasExecutableItems = true;
        }
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
                    if (hasExecutableItems && entry.dropData.containsKey("display_name")) {
                        String displayName = (String) entry.dropData.get("display_name");
                        if (displayName.startsWith("EI_")) {
                            String itemNameWithoutPrefix = displayName.substring(3);

                            Optional<ExecutableItemInterface> eiOpt = ExecutableItemsAPI.getExecutableItemsManager().getExecutableItem(itemNameWithoutPrefix);

                            if (eiOpt.isPresent()) {
                                ItemStack item = eiOpt.get().buildItem(1, Optional.empty(), Optional.empty());
                                return Optional.of(item);
                            }
                        }
                    }

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
