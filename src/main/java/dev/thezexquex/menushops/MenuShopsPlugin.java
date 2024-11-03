package dev.thezexquex.menushops;

import dev.thezexquex.menushops.command.AdditionalCaptionKeys;
import dev.thezexquex.menushops.command.MenuShopCommand;
import dev.thezexquex.menushops.configuration.ConfigurationLoader;
import dev.thezexquex.menushops.configuration.typeserializer.icon.IconTypeSerializer;
import dev.thezexquex.menushops.data.ShopService;
import dev.thezexquex.menushops.hooks.PluginHookService;
import dev.thezexquex.menushops.hooks.externalhooks.CoinsEngineHook;
import dev.thezexquex.menushops.hooks.externalhooks.PlaceholderApiHook;
import dev.thezexquex.menushops.hooks.externalhooks.VaultHook;
import dev.thezexquex.menushops.message.Messenger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.caption.CaptionProvider;
import org.incendo.cloud.caption.StandardCaptionKeys;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.NodePath;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Level;

public class MenuShopsPlugin extends JavaPlugin {
    private LegacyPaperCommandManager<CommandSender> commandManager;
    private ShopService shopService;
    private PluginHookService pluginHookService;
    private Messenger messenger;
    private Economy vaultEconomy;
    private final HashMap<Character, ItemStack> icons = new HashMap<>();

    @Override
    public void onEnable() {
        registerCommands();

        reload();

        new MenuShopCommand(this).register(commandManager);
    }

    public void reload() {
        this.pluginHookService = new PluginHookService(getServer());
        this.pluginHookService.register(this, new PlaceholderApiHook());
        this.pluginHookService.register(this, new CoinsEngineHook());
        this.pluginHookService.register(this, new VaultHook());

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
    }

    private void registerCommands() {
        try {
            this.commandManager = new LegacyPaperCommandManager<>(
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
        saveResourceFile(Path.of("icons.yml"));

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
        ConfigurationLoader configurationLoader = new ConfigurationLoader(this);

        var messageConfigurationLoader = YamlConfigurationLoader.builder()
                .path(getDataFolder().toPath().resolve("messages.yml"))
                .build();

        var iconsConfigurationLoader = YamlConfigurationLoader.builder()
                .defaultOptions(build -> build.serializers(builder -> builder.register(ItemStack.class, new IconTypeSerializer())))
                .path(getDataFolder().toPath().resolve("icons.yml"))
                .build();

        try {
            var iconsRootNode = iconsConfigurationLoader.load();

            var iconNodes =  iconsRootNode.childrenMap();

            for (Object key : iconNodes.keySet()) {
                icons.put(String.valueOf(key).charAt(0), iconNodes.get(key).get(ItemStack.class));
            }

        } catch (ConfigurateException e) {
            getLogger().log(Level.WARNING, "Failed to load icons configuration", e);
        }

        var messageNodeOpt = configurationLoader.loadConfiguration(messageConfigurationLoader);
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

    public HashMap<Character, ItemStack> icons() {
        return icons;
    }

    public Economy vaultEconomy() {
        return vaultEconomy;
    }

    public void vaultEconomy(Economy vaultEconomy) {
        this.vaultEconomy = vaultEconomy;
    }
}