package dev.reloadx;

import dev.reloadx.commands.GiveItemCommand;
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
        saveResource("messages.yml", false); // Guardar el archivo messages.yml si no existe

        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);

        // Registramos el comando
        GiveItemCommand giveItemCommand = new GiveItemCommand(this, new CustomLootConfig(this), messageUtils);
        getCommand("giveitem").setExecutor(giveItemCommand);

        // Otras configuraciones
        new CommandManager(this, messageUtils, messagesConfig);
        CustomLootConfig lootConfig = new CustomLootConfig(this);
        getServer().getPluginManager().registerEvents(new MobDeathListener(lootConfig), this);
    }
}
