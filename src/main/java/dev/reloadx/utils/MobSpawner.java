package dev.reloadx.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.logging.Logger;

public class MobSpawner {
    private final Logger logger;

    public MobSpawner(Logger logger) {
        this.logger = logger;
    }

    public void spawnMob(Location location, Map<?, ?> mobConfig) {
        if (!mobConfig.containsKey("mob")) {
            return;
        }

        String mobTypeStr = (String) mobConfig.get("mob");
        EntityType mobType;
        try {
            mobType = EntityType.valueOf(mobTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        World world = location.getWorld();
        if (world == null) {
            return;
        }

        LivingEntity entity = (LivingEntity) world.spawnEntity(location, mobType);

        if (mobConfig.containsKey("display_name")) {
            entity.setCustomName((String) mobConfig.get("display_name"));
            entity.setCustomNameVisible(true);
        }

        if (mobConfig.containsKey("equipment")) {
            setEntityEquipment(entity, (Map<?, ?>) mobConfig.get("equipment"));
        }
    }

    private static void setEntityEquipment(LivingEntity entity, Map<?, ?> equipmentConfig) {
        if (equipmentConfig.containsKey("helmet")) {
            entity.getEquipment().setHelmet(new ItemStack(org.bukkit.Material.valueOf(((String) equipmentConfig.get("helmet")).toUpperCase())));
        }
        if (equipmentConfig.containsKey("chestplate")) {
            entity.getEquipment().setChestplate(new ItemStack(org.bukkit.Material.valueOf(((String) equipmentConfig.get("chestplate")).toUpperCase())));
        }
        if (equipmentConfig.containsKey("leggings")) {
            entity.getEquipment().setLeggings(new ItemStack(org.bukkit.Material.valueOf(((String) equipmentConfig.get("leggings")).toUpperCase())));
        }
        if (equipmentConfig.containsKey("boots")) {
            entity.getEquipment().setBoots(new ItemStack(org.bukkit.Material.valueOf(((String) equipmentConfig.get("boots")).toUpperCase())));
        }
        if (equipmentConfig.containsKey("weapon")) {
            entity.getEquipment().setItemInMainHand(new ItemStack(org.bukkit.Material.valueOf(((String) equipmentConfig.get("weapon")).toUpperCase())));
        }
    }
}

