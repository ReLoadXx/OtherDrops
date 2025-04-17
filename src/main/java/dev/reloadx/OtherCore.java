package dev.reloadx;

import dev.reloadx.commands.CommandManager;
import dev.reloadx.commands.OtherDropsCommandManager;
import dev.reloadx.commands.OtherFishingCommandManager;
import dev.reloadx.commands.OtherBlocksCommandManager;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.config.OtherFishingConfig;
import dev.reloadx.config.OtherBlocksConfig;
import dev.reloadx.listeners.MobDeathListener;
import dev.reloadx.listeners.FishingListener;
import dev.reloadx.listeners.BlockListener;
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.utils.StartupManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherCore extends JavaPlugin {

    private OtherFishingConfig otherFishingConfig;
    private OtherBlocksConfig otherBlocksConfig;
    private static boolean hasExecutableItems = false;

    @Override
    public void onEnable() {
        otherFishingConfig = new OtherFishingConfig(this);
        otherBlocksConfig = new OtherBlocksConfig(this);

        StartupManager.onStartup(getLogger(), this);

        saveDefaultConfig();
        saveResource("messages.yml", false);

        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);

        OtherDropsConfig configManager = new OtherDropsConfig(this);

        new CommandManager(this, messageUtils, messagesConfig, configManager, otherFishingConfig, otherBlocksConfig);
        new OtherFishingCommandManager(this, otherFishingConfig, messageUtils);
        new OtherDropsCommandManager(this, configManager, messageUtils);
        new OtherBlocksCommandManager(this, otherBlocksConfig, messageUtils);

        getServer().getPluginManager().registerEvents(new MobDeathListener(configManager), this);
        getServer().getPluginManager().registerEvents(new FishingListener(this, otherFishingConfig), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this, otherBlocksConfig), this);

        Plugin executableItems = getServer().getPluginManager().getPlugin("ExecutableItems");
        if (executableItems != null && executableItems.isEnabled()) {
            getLogger().info("[OtherCore] Executable Items detectado y vinculado!");
            hasExecutableItems = true;
        }
    }
}
