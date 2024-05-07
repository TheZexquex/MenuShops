package dev.thezexquex.menushops.configuration;

import dev.thezexquex.menushops.MenuShopsPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Level;

public class ConfigurationLoader {
    private final MenuShopsPlugin plugin;
    private ConfigurationNode mainConfigRootNode;
    private ConfigurationNode messageConfigRootNode;

    public ConfigurationLoader(MenuShopsPlugin plugin) {
        this.plugin = plugin;
    }

    public Optional<ConfigurationNode> loadConfiguration(AbstractConfigurationLoader<CommentedConfigurationNode> configurationLoader) {
        try {
            return Optional.of(configurationLoader.load());
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load messages.yml", e);
        }
        return Optional.empty();
    }

    public void saveDefaultConfigFile(Path filePath) {
        var completePath = plugin.getDataFolder().toPath().resolve(filePath);
        if (Files.exists(completePath)) {
            return;
        }

        try (var resourceAsStream = getClass().getResourceAsStream("/" + filePath)) {
            if (resourceAsStream == null) {
                plugin.getLogger().log(
                        Level.SEVERE, "Failed to save " + filePath + ". " +
                                "The plugin developer tried to save a file that does not exist in the plugins jar file!"
                );
                return;
            }
            Files.createDirectories(completePath.getParent());
            try (var outputStream = Files.newOutputStream(completePath)) {
                resourceAsStream.transferTo(outputStream);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save " + filePath, e);
        }
    }

    public void saveConfiguration(
            ConfigurationNode configurationNode,
            Configuration configuration,
            org.spongepowered.configurate.loader.ConfigurationLoader<ConfigurationNode> configurationLoader
    ) {
        try {
            configurationNode.set(configuration);
            configurationLoader.save(configurationNode);
        } catch (ConfigurateException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save configuration", e);
        }
    }
}
