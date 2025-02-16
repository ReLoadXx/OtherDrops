package dev.reloadx.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Logger;

public class MobSpawner {
    private final Logger logger;

    public MobSpawner(Logger logger) {
        this.logger = logger;
    }

    public void spawnMob(Location location, ConfigurationSection mobConfig) {
        if (!mobConfig.contains("mob")) {
            return;
        }

        String mobTypeStr = mobConfig.getString("mob").toUpperCase();
        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobTypeStr);
        } catch (IllegalArgumentException e) {
            logger.warning("Tipo de mob inválido: " + mobTypeStr);
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        LivingEntity entity = (LivingEntity) world.spawnEntity(location, mobType);

        if (mobConfig.contains("display_name")) {
            entity.setCustomName(ColorUtils.hex(mobConfig.getString("display_name")));
            entity.setCustomNameVisible(true);
        }

        if (mobConfig.contains("equipment")) {
            setEntityEquipment(entity, mobConfig.getConfigurationSection("equipment"));
        }
    }

    private static void setEntityEquipment(LivingEntity entity, ConfigurationSection equipmentConfig) {
        if (equipmentConfig == null) return;

        if (equipmentConfig.contains("helmet")) {
            entity.getEquipment().setHelmet(ItemUtils.createItem(equipmentConfig.getConfigurationSection("helmet")));
        }
        if (equipmentConfig.contains("chestplate")) {
            entity.getEquipment().setChestplate(ItemUtils.createItem(equipmentConfig.getConfigurationSection("chestplate")));
        }
        if (equipmentConfig.contains("leggings")) {
            entity.getEquipment().setLeggings(ItemUtils.createItem(equipmentConfig.getConfigurationSection("leggings")));
        }
        if (equipmentConfig.contains("boots")) {
            entity.getEquipment().setBoots(ItemUtils.createItem(equipmentConfig.getConfigurationSection("boots")));
        }
        if (equipmentConfig.contains("weapon")) {
            entity.getEquipment().setItemInMainHand(ItemUtils.createItem(equipmentConfig.getConfigurationSection("weapon")));
        }
    }
}
