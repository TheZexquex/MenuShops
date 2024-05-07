package dev.thezexquex.menushops;

import dev.thezexquex.menushops.command.MenuShopCommand;
import dev.thezexquex.menushops.configuration.ConfigurationLoader;
import dev.thezexquex.menushops.data.shop.ShopService;
import dev.thezexquex.menushops.hooks.PluginHookService;
import dev.thezexquex.menushops.hooks.externalhooks.PlaceholderApiHook;
import dev.thezexquex.menushops.message.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class MenuShopsPlugin extends JavaPlugin {
    private PaperCommandManager<CommandSender> commandManager;
    private ShopService shopService;
    private PluginHookService pluginHookService;
    private ConfigurationLoader configurationLoader;
    private Messenger messenger;

    @Override
    public void onEnable() {
        registerCommands();
    }

    private void registerCommands() {
        try {
            this.commandManager = new PaperCommandManager<>(
                    this,
                    ExecutionCoordinator.simpleCoordinator(),
                    SenderMapper.identity()
            );

            if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
                commandManager.registerBrigadier();
            }
        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        this.pluginHookService.register(this, new PlaceholderApiHook());

        this.shopService = new ShopService(getDataFolder().toPath().resolve("shops"), getLogger());
        shopService.loadAllShops();

        new MenuShopCommand(this).register(commandManager);
    }

    private void loadConfiguration() {
        this.configurationLoader = new ConfigurationLoader(this);

        var messageConfigurationBuilder = YamlConfigurationLoader.builder()
                .path(getDataFolder().toPath().resolve("messages.yml"))
                .build();

        var messageNodeOpt = configurationLoader.loadConfiguration(messageConfigurationBuilder);
        messageNodeOpt.ifPresent(configurationNode -> this.messenger = new Messenger(this, configurationNode));
    }

    public ShopService shopService() {
        return shopService;
    }

    public PluginHookService pluginHookService() {
        return pluginHookService;
    }

    public Messenger messenger() {
        return messenger;
    }
}