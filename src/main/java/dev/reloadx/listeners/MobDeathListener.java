package dev.reloadx.listeners;

import dev.reloadx.config.CustomLootConfig;
import dev.reloadx.utils.ColorUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

public class MobDeathListener implements Listener {
    private final CustomLootConfig lootConfig;
    private final Random random = new Random();
    private final Logger logger;

    public MobDeathListener(CustomLootConfig lootConfig) {
        this.lootConfig = lootConfig;
        this.logger = lootConfig.getPlugin().getLogger();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                ConfigurationSection items = lootConfig.getItems();

                logger.info("Comprobando drops para el item: " + displayName);

                if (items != null) {
                    for (String itemKey : items.getKeys(false)) {
                        ConfigurationSection itemData = items.getConfigurationSection(itemKey);
                        if (itemData != null) {
                            String configDisplayName = itemData.getString("display_name");

                            if (displayName.equals(configDisplayName)) {
                                logger.info("Item coincide con la configuración: " + displayName);
                                ConfigurationSection mobs = itemData.getConfigurationSection("mobs");
                                if (mobs != null) {
                                    EntityType mobType = event.getEntityType();
                                    String mobKey = mobType.toString();

                                    if (mobs.contains(mobKey)) {
                                        logger.info("Configuración encontrada para el mob: " + mobKey);
                                        List<Map<?, ?>> drops = mobs.getMapList(mobKey + ".drops");
                                        if (drops != null && !drops.isEmpty()) {
                                            logger.info("Cantidad de drops encontrados: " + drops.size());
                                            processDrops(event, drops);
                                        } else {
                                            logger.warning("No se encontraron drops para el mob: " + mobKey);
                                        }
                                    } else {
                                        logger.warning("No se encontró configuración para el mob: " + mobKey);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void processDrops(EntityDeathEvent event, List<Map<?, ?>> drops) {
        for (Map<?, ?> dropMap : drops) {
            double chance = (double) dropMap.get("chance");
            logger.info("Procesando drop con probabilidad: " + chance + "%");

            if (random.nextDouble() * 100 <= chance) {
                String itemType = (String) dropMap.get("item");
                int quantity = (int) dropMap.get("quantity");

                Material material = Material.getMaterial(itemType);
                if (material != null) {
                    ItemStack dropItem = new ItemStack(material, quantity);

                    String displayName = (String) dropMap.get("display_name");
                    List<String> lore = (List<String>) dropMap.get("lore");

                    ItemMeta meta = dropItem.getItemMeta();
                    if (meta != null) {
                        if (displayName != null) {
                            meta.setDisplayName(ColorUtils.hex(displayName));
                        }
                        if (lore != null) {
                            lore = lore.stream()
                                    .map(ColorUtils::hex)
                                    .toList();
                            meta.setLore(lore);
                        }
                        dropItem.setItemMeta(meta);
                    }

                    event.getDrops().add(dropItem);
                    logger.info("Item dropeado: " + material.name() + " x" + quantity);
                } else {
                    logger.severe("Error: Material no válido: " + itemType);
                }
            } else {
                logger.info("Drop no realizado debido a la probabilidad.");
            }
        }
    }

}