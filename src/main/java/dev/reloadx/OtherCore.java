package dev.reloadx;

import dev.reloadx.commands.CommandManager;
import dev.reloadx.commands.OtherDropsCommandManager;
import dev.reloadx.commands.OtherFishingCommandManager;
import dev.reloadx.commands.OtherArmorCommandManager;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.config.OtherArmorConfig;
import dev.reloadx.listeners.MobDeathListener;
import dev.reloadx.listeners.FishingListener;
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.utils.StartupManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherCore extends JavaPlugin {

    private OtherFishingConfig otherFishingConfig;
    private OtherArmorConfig otherArmorConfig;

    @Override
    public void onEnable() {

        otherFishingConfig = new OtherFishingConfig(this);
        otherArmorConfig = new OtherArmorConfig(this);

        StartupManager.onStartup(getLogger(), this);
        saveDefaultConfig();
        saveResource("messages.yml", false);

        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);

        OtherDropsConfig configManager = new OtherDropsConfig(this);
        new OtherFishingCommandManager(this, otherFishingConfig, messageUtils);
        new OtherDropsCommandManager(this, configManager, messageUtils);

        new OtherArmorCommandManager(this, otherArmorConfig, messageUtils);

        getServer().getPluginManager().registerEvents(new MobDeathListener(configManager), this);
        getServer().getPluginManager().registerEvents(new FishingListener(this, otherFishingConfig), this);

    }

    public OtherFishingConfig getOtherFishingConfig() {
        return otherFishingConfig;
    }

    public OtherArmorConfig getOtherArmorConfig() {
        return otherArmorConfig;
    }
}
