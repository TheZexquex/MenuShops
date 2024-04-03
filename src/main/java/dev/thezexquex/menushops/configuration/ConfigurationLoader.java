package dev.thezexquex.menushops.configuration;

public class ConfigurationLoader {
    /*
    private final EssentialsPlugin plugin;
    private HoconConfigurationLoader mainConfigurationLoader;
    private YamlConfigurationLoader messageConfigurationLoader;
    private ConfigurationNode mainConfigRootNode;
    private ConfigurationNode messageConfigRootNode;

    public ConfigurationLoader(EssentialsPlugin plugin) {
        this.plugin = plugin;
    }

    public void intiConfigurationLoader() {
        var mainConfigPath = plugin.getDataFolder().toPath().resolve(Path.of("config.conf"));
        mainConfigurationLoader = HoconConfigurationLoader
                .builder()
                .path(mainConfigPath)
                .defaultOptions(opts -> opts.serializers(build -> build.register(Configuration.class, new ConfigurationTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(SpawnSettings.class, new SpawnSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(DataBaseSettings.class, new DatabaseSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(RedisSettings.class, new RedisSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(CountDownLine.class, new CountDownLineTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(CountDownSettings.class, new CountDownSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(TeleportSettings.class, new TeleportSettingsTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(AFKSettings.class, new AFKSettingsTypeSerializer())))
                .build();

        var messageConfigPath = plugin.getDataFolder().toPath().resolve(Path.of("messages.yml"));
        messageConfigurationLoader = YamlConfigurationLoader.builder().path(messageConfigPath).build();
    }

    public ConfigurationNode loadConfiguration() {
        try {
            mainConfigRootNode = mainConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load config.conf", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        return mainConfigRootNode;
    }

    public ConfigurationNode loadMessageConfiguration() {
        try {
            messageConfigRootNode = messageConfigurationLoader.load();
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load messages.yml", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
        return messageConfigRootNode;
    }

    public void saveDefaultConfigs() {
        plugin.saveResource("config.conf", false);
        plugin.saveResource("messages.yml", false);
    }

    public void saveConfiguration() {
        try {
            mainConfigRootNode.set(plugin.configuration());
            mainConfigurationLoader.save(mainConfigRootNode);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.conf", e);
        }
    }

    public ConfigurationNode mainConfigRootNode() {
        return mainConfigRootNode;
    }

     */
}
