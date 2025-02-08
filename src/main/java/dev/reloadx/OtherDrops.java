package dev.reloadx;

import dev.reloadx.commands.CommandManager;
import dev.reloadx.commands.OtherDropsCommandManager;
import dev.reloadx.config.MessagesConfig;
import dev.reloadx.config.OtherDropsConfig;
import dev.reloadx.config.OtherFishingConfig;  // Importa la clase de configuración de fishing
import dev.reloadx.listeners.MobDeathListener;
import dev.reloadx.listeners.FishingListener;  // Asegúrate de importar el listener adecuado
import dev.reloadx.utils.MessageUtils;
import dev.reloadx.utils.StartupManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OtherDrops extends JavaPlugin {

    private OtherFishingConfig otherFishingConfig;  // Añadir una propiedad para la configuración

    @Override
    public void onEnable() {

        // Inicializar la configuración de "OtherFishing"
        otherFishingConfig = new OtherFishingConfig(this);  // Cargar la configuración de otherfishing.yml

        // Realizar las configuraciones predeterminadas y cargar otros recursos
        StartupManager.onStartup(getLogger(), this);
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // Inicializar configuraciones
        MessagesConfig messagesConfig = new MessagesConfig(this);
        MessageUtils messageUtils = new MessageUtils(messagesConfig);
        OtherDropsConfig configManager = new OtherDropsConfig(this);

        // Inicializar y registrar comandos
        new OtherDropsCommandManager(this, configManager, messageUtils);
        new CommandManager(this, messageUtils, messagesConfig);

        // Registrar los eventos
        getServer().getPluginManager().registerEvents(new MobDeathListener(configManager), this);

        // Registra el listener de pesca, pasando la configuración de OtherFishingConfig
        getServer().getPluginManager().registerEvents(new FishingListener(this, otherFishingConfig), this);
    }

    // Método para obtener la configuración de OtherFishingConfig desde otros componentes
    public OtherFishingConfig getOtherFishingConfig() {
        return otherFishingConfig;
    }
}
