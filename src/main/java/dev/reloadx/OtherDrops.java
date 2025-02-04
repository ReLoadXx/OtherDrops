package dev.reloadx;

import dev.reloadx.commands.CommandManager;
import dev.reloadx.commands.OtherDropsCommandManager;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.listeners.MobDeathListener;
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.utils.StartupManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("OtherDrops plugin activado.");

        StartupManager.onStartup(getLogger(), this);

        saveDefaultConfig();
        saveResource("messages.yml", false);

        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);

        OtherDropsConfig configManager = new OtherDropsConfig(this);
        new OtherDropsCommandManager(this, configManager);

        new CommandManager(this, messageUtils, messagesConfig);

        getServer().getPluginManager().registerEvents(new MobDeathListener(configManager), this);

    }

}
