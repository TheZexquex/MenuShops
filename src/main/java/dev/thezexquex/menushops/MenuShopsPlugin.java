package dev.thezexquex.menushops;

import dev.thezexquex.menushops.command.AdditionalCaptionKeys;
import dev.thezexquex.menushops.command.MenuShopCommand;
import dev.thezexquex.menushops.configuration.ConfigurationLoader;
import dev.thezexquex.menushops.data.shop.ShopService;
import dev.thezexquex.menushops.hooks.PluginHookService;
import dev.thezexquex.menushops.hooks.externalhooks.PlaceholderApiHook;
import dev.thezexquex.menushops.message.Messenger;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.brigadier.BrigadierSetting;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
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

        this.pluginHookService = new PluginHookService(getServer());
        this.pluginHookService.register(this, new PlaceholderApiHook());

        var shopConfigsFolder = getDataFolder().toPath().resolve("shops");

        if (!setUpFoldersAndFiles(shopConfigsFolder)) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loadConfiguration();

        this.shopService = new ShopService(shopConfigsFolder, getLogger());
        try {
            shopService.loadAllShops();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Failed to load shop configurations", e);
        }

        new MenuShopCommand(this).register(commandManager);
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

        var registry = commandManager.captionRegistry();

        
        registry.registerProvider(
                CaptionProvider.forCaption(AdditionalCaptionKeys.ARGUMENT_PARSE_FAILURE_VALUE,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "value")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_CHAR,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "char")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "color")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_DURATION,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "duration")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "enum")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_NUMBER,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "number")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "string")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_REGEX,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "regex")))
        );
        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "uuid")))
        );

        registry.registerProvider(
                CaptionProvider.forCaption(StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_BOOLEAN,
                sender -> messenger.getString(NodePath.path("exception", "argument-parse", "boolean")))
        );
    }

    private boolean setUpFoldersAndFiles(Path shopConfigsFolder) {
        saveResourceFile(Path.of("messages.yml"));

        try {
            Files.createDirectory(shopConfigsFolder);
        } catch (FileAlreadyExistsException e) {
            return true;
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to create folder " + shopConfigsFolder, e);
            return false;
        }
        return true;
    }

    private void saveResourceFile(Path path) {
        if (getDataFolder().toPath().resolve(path).toFile().exists()) {
            return;
        }

        saveResource(path.toString(), false);
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