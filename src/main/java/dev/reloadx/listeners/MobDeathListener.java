package dev.reloadx.listeners;

import dev.reloadx.config.OtherDropsConfig;
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
    private final OtherDropsConfig lootConfig;
    private final Random random = new Random();
    private final Logger logger;

    public MobDeathListener(OtherDropsConfig lootConfig) {
        this.lootConfig = lootConfig;
        this.logger = lootConfig.getPlugin().getLogger();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        logger.info("Evento de muerte recibido: " + event.getEntity().getName());

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            ItemMeta meta = itemInHand.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                logger.info("Jugador: " + player.getName() + " ha matado un mob con el item: " + displayName);

                ConfigurationSection items = lootConfig.getConfig().getConfigurationSection("items");
                if (items != null) {
                    logger.info("Se encontraron " + items.getKeys(false).size() + " items configurados en 'items'.");

                    for (String itemKey : items.getKeys(false)) {
                        ConfigurationSection itemData = items.getConfigurationSection(itemKey);
                        if (itemData != null) {
                            String configDisplayName = itemData.getString("display_name");
                            logger.info("Verificando item: " + itemKey + " con display_name: " + configDisplayName);

                            if (displayName.equals(configDisplayName)) {
                                logger.info("Item coincide con la configuración: " + displayName);
                                ConfigurationSection mobs = itemData.getConfigurationSection("mobs");

                                if (mobs != null) {
                                    EntityType mobType = event.getEntityType();
                                    String mobKey = mobType.toString();
                                    logger.info("Mob muerto: " + mobKey);

                                    if (mobs.contains(mobKey)) {
                                        logger.info("Configuración encontrada para el mob: " + mobKey);
                                        List<Map<?, ?>> drops = mobs.getMapList(mobKey + ".drops");

                                        if (drops != null && !drops.isEmpty()) {
                                            logger.info("Cantidad de drops encontrados para el mob " + mobKey + ": " + drops.size());
                                            processDrops(event, drops);
                                        } else {
                                            logger.warning("No se encontraron drops para el mob: " + mobKey);
                                        }
                                    } else {
                                        logger.warning("No se encontró configuración para el mob: " + mobKey);
                                    }
                                } else {
                                    logger.warning("No se encontró configuración de mobs en el item: " + itemKey);
                                }
                            } else {
                                logger.info("El display_name no coincide con la configuración: " + displayName + " != " + configDisplayName);
                            }
                        }
                    }
                } else {
                    logger.warning("No se encontró la sección 'items' en la configuración.");
                }
            } else {
                logger.warning("El item en la mano del jugador no tiene un nombre.");
            }
        } else {
            logger.warning("El evento de muerte no tiene un killer.");
        }
    }

    private void processDrops(EntityDeathEvent event, List<Map<?, ?>> drops) {
        logger.info("Procesando los drops...");

        for (Map<?, ?> dropMap : drops) {
            double chance = (double) dropMap.get("chance");
            logger.info("Probabilidad del drop: " + chance + "%");

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
                            logger.info("Estableciendo displayName: " + displayName);
                        }
                        if (lore != null) {
                            lore = lore.stream()
                                    .map(ColorUtils::hex)
                                    .toList();
                            meta.setLore(lore);
                            logger.info("Estableciendo lore: " + lore);
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
