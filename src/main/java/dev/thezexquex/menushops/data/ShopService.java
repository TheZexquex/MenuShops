package dev.thezexquex.menushops.data;

import dev.thezexquex.menushops.configuration.typeserializer.shop.MenuShopTypeSerializer;
import dev.thezexquex.menushops.configuration.typeserializer.shop.ShopItemTypeSerializer;
import dev.thezexquex.menushops.configuration.typeserializer.shop.ValueTypeSerializer;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.value.Value;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShopService {
    private final Map<MenuShop, Path> loadedShops;
    private final Path shopConfigsFolder;
    private final Logger logger;

    public ShopService(Path shopConfigsFolder, Logger logger) {
        this.loadedShops = new HashMap<>();
        this.shopConfigsFolder = shopConfigsFolder;
        this.logger = logger;
    }

    public void loadAllShops() throws IOException {
        try (var paths = Files.list(shopConfigsFolder)) {
            var fileNames = paths.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .map(File::getName)
                    .filter(fileName -> fileName.endsWith(".yml"))
                    .toList();

            if (fileNames.isEmpty()) {
                logger.info("No shop configs found...");
                return;
            }

            fileNames.forEach(fileName -> {
                logger.info("Found shop file: '" + fileName + "' Attempting to load shop...");
                loadShop(fileName);
            });
        }
    }

    private void loadShop(String fileName) {
        var shopIdentifier = fileName.replace(".yml", "");
        var shopPath = shopConfigsFolder.resolve(Paths.get(fileName));
        var shopConfigLoader = shopConfigLoader(shopPath);

        try {
            var rootNode = shopConfigLoader.load();
            var menuShop = rootNode.get(MenuShop.class);
            if (menuShop == null) {
                logger.log(Level.WARNING, "Failed to load shop: '" + shopIdentifier + "' Null");
                return;
            }

            menuShop.identifier(shopIdentifier);

            loadedShops.put(menuShop, shopPath);

            logger.info("Successfully finished loading shop: '" + shopIdentifier + "'");
        } catch (ConfigurateException e) {
            logger.log(Level.WARNING, "Failed to load shop: '" + shopIdentifier + "'", e);
        }
    }

    public Map<MenuShop, Path> loadedShops() {
        return loadedShops;
    }

    public List<String> loadedShopNames() {
        return loadedShops.keySet().stream().map(MenuShop::identifier).toList();
    }

    public boolean createShop(String shopName, String shopTitle) {
        var shop = new MenuShop(shopName, MiniMessage.miniMessage().deserialize(shopTitle));

        var shopPath = shopConfigsFolder.resolve(Paths.get(shopName + ".yml"));

        try {
            if (!shopPath.toFile().createNewFile()) {
                return false;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to create shop " + shopName, e);
            return false;
        }
        loadedShops.put(shop, shopPath);
        return saveShop(shop, shopPath);
    }

    public boolean saveShop(MenuShop menuShop, Path shopPath) {
        var shopConfigLoader = shopConfigLoader(shopPath);
        try {
            var shopNode = shopConfigLoader.load();

            shopNode.set(menuShop);
            shopConfigLoader.save(shopNode);

            return true;
        } catch (ConfigurateException e) {
            logger.log(Level.WARNING, "Failed to create shop " + menuShop.identifier(), e);
            return false;
        }
    }

    public boolean saveShop(MenuShop menuShop) {
        return saveShop(menuShop, loadedShops.get(menuShop));
    }

    private YamlConfigurationLoader shopConfigLoader(Path shopPath) {
        return YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ShopItem.class, new ShopItemTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(Value.class, new ValueTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(builder -> builder.register(MenuShop.class, new MenuShopTypeSerializer())))
                .nodeStyle(NodeStyle.BLOCK)
                .path(shopPath)
                .build();
    }

    public boolean deleteShop(String shopName) {
        var shopOpt = getShop(shopName);

        if (shopOpt.isEmpty()) {
            return false;
        }

        loadedShops.remove(shopOpt.get());
        return shopConfigsFolder.resolve(Paths.get(shopName + ".yml")).toFile().delete();
    }

    public Optional<MenuShop> getShop(String shopName) {
        return loadedShops.keySet().stream().filter(menuShop -> menuShop.identifier().equals(shopName)).findFirst();
    }
}
