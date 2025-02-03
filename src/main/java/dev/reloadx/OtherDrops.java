package dev.reloadx;

import dev.reloadx.commands.CommandManager;
import dev.reloadx.config.CustomLootConfig;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.listeners.MobDeathListener;
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.utils.StartupManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherDrops extends JavaPlugin {

    @Override
    public void onEnable() {
        StartupManager.onStartup(getLogger(), this);

        saveDefaultConfig();
        saveResource("messages.yml", false);

        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);

        CustomLootConfig lootConfig = new CustomLootConfig(this);
        getServer().getPluginManager().registerEvents(new MobDeathListener(lootConfig), this);

        new CommandManager(this, messageUtils, messagesConfig);
    }
}
