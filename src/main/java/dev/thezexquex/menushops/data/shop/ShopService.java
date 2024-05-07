package dev.thezexquex.menushops.data.shop;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import de.eldoria.jacksonbukkit.JacksonPaper;
import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
import dev.thezexquex.menushops.utils.MiniComponent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShopService {
    private final Set<MenuShop> loadedShops;
    private final Path shopConfigsFolder;
    private final Logger logger;

    public ShopService(Path shopConfigsFolder, Logger logger) {
        this.loadedShops = new HashSet<>();
        this.shopConfigsFolder = shopConfigsFolder;
        this.logger = logger;
    }

    public void loadAllShops() {;
        var fileNames = shopConfigsFolder.toFile().list();

        if (fileNames == null) {
            logger.info("No shop configs found...");
            return;
        }

        Arrays.stream(fileNames).filter(fileName -> fileName.endsWith(".yml")).forEach(fileName -> {
            logger.info("Found shop file: " + fileName + " attempting to load shop");
            loadShop(fileName);
        });
    }

    private void loadShop(String fileName) {
        var shopIdentifyer = fileName.replace(".yml", "");
        var shopPath = shopConfigsFolder.resolve(Paths.get(fileName));
        var shopLoader =YamlConfigurationLoader.builder()
                .defaultOptions(opts -> opts.serializers(build -> build.register(ShopItem.class, new ShopItemTypeSerializer())))
                .defaultOptions(opts -> opts.serializers(build -> build.register(Value.class, new ValueTypeSerializer())))
                .path(shopPath)
                .build();
        try {
            var rootNode = shopLoader.load();
            var title = MiniComponent.of(rootNode.node("title").getString());
            var items = new HashMap<Integer, ShopItem>();

            rootNode.node("items").childrenMap().forEach((id, itemNode) -> {
                try {
                    items.put(Integer.parseInt(id.toString()), itemNode.get(ShopItem.class));
                } catch (SerializationException e) {
                    logger.log(Level.WARNING, "Failed to load shop: " + shopIdentifyer, e);
                }
            });

            loadedShops.add(new MenuShop(shopIdentifyer, title, items));

            logger.info("Loaded shop: " + shopIdentifyer);
        } catch (ConfigurateException e) {
            logger.log(Level.WARNING, "Failed to load shop: " + shopIdentifyer, e);
        }
    }

    public Set<MenuShop> loadedShops() {
        return loadedShops;
    }

    public List<String> loadedShopNames() {
        return loadedShops.stream().map(MenuShop::identifyer).toList();
    }

    public void createShop(String shopName) {

    }

    public boolean deleteShop(String shopName) {
        loadedShops.removeIf(menuShop -> menuShop.identifyer().equals(shopName));
        return shopConfigsFolder.resolve(Paths.get(shopName + ".yml")).toFile().delete();
    }

    public Optional<MenuShop> getShop(String shopName) {
        return loadedShops.stream().filter(menuShop -> menuShop.identifyer().equals(shopName)).findFirst();
    }
}
