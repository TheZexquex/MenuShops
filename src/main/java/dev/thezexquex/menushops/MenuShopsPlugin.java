package dev.thezexquex.menushops;

import dev.thezexquex.menushops.command.CommandCapabilities;
import dev.thezexquex.menushops.command.CommandCaptions;
import dev.thezexquex.menushops.command.MenuShopCommand;
import dev.thezexquex.menushops.configuration.ConfigurationLoader;
import dev.thezexquex.menushops.configuration.typeserializer.icon.IconTypeSerializer;
import dev.thezexquex.menushops.data.ShopService;
import dev.thezexquex.menushops.hooks.PluginHookRegistry;
import dev.thezexquex.menushops.hooks.externalhooks.CoinsEngineHook;
import dev.thezexquex.menushops.hooks.externalhooks.PlaceholderApiHook;
import dev.thezexquex.menushops.hooks.externalhooks.VaultHook;
import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.value.ValueRegistry;
import dev.thezexquex.menushops.shop.value.type.MaterialValueType;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.spongepowered.configurate.ConfigurateException;
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
    private PluginHookRegistry pluginHookRegistry;
    private Messenger messenger;
    private Economy vaultEconomy;
    private final HashMap<Character, ItemStack> icons = new HashMap<>();
    private ValueRegistry valueRegistry;

    @Override
    public void onEnable() {

        reload();

        registerCommands();
    }

    public void reload() {
        this.valueRegistry = new ValueRegistry();
        valueRegistry.registerValue(new MaterialValueType());

        this.pluginHookRegistry = new PluginHookRegistry();
        this.pluginHookRegistry.register(this, new PlaceholderApiHook());
        this.pluginHookRegistry.register(this, new CoinsEngineHook());
        this.pluginHookRegistry.register(this, new VaultHook());

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

            CommandCapabilities.register(commandManager, messenger);

        } catch (Exception e) {
            this.getLogger().log(Level.SEVERE, "Failed to initialize command manager", e);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        CommandCaptions.apply(commandManager.captionRegistry(), messenger);

        new MenuShopCommand(this).register(commandManager);
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

            var iconNodes = iconsRootNode.childrenMap();

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

    public PluginHookRegistry pluginHookService() {
        return pluginHookRegistry;
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

    public ValueRegistry valueRegistry() {
        return valueRegistry;
    }
}