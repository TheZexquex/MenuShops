package dev.thezexquex.menushops.configuration.typeserializer.shop;

import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.gui.DefaultValues;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

public class MenuShopTypeSerializer implements TypeSerializer<MenuShop> {

    @Override
    public MenuShop deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var titleString = node.node("title").getString();
        var title = MiniMessage.miniMessage().deserialize(titleString == null ? "<red>N/A title" : titleString);
        var outerStructureList = node.node("structure-outer").getList(String.class);
        var innerStructureList = node.node("structure-inner").getList(String.class);

        var sellsItems = new LinkedList<>(Objects.requireNonNull(node.node("shop-sells-items").getList(ShopItem.class)));
        var buysItems = new LinkedList<>(Objects.requireNonNull(node.node("shop-buys-items").getList(ShopItem.class)));


        return new MenuShop(
                "",
                title,
                sellsItems,
                buysItems,
                (outerStructureList == null) || (outerStructureList.isEmpty()) ?
                        DefaultValues.STANDARD_STRUCTURE_OUTER :
                        outerStructureList.toArray(String[]::new),
                (innerStructureList == null) || (innerStructureList.isEmpty()) ?
                        DefaultValues.STANDARD_STRUCTURE_INNER :
                        innerStructureList.toArray(String[]::new)
        );
    }

    @Override
    public void serialize(Type type, @Nullable MenuShop menuShop, ConfigurationNode node) throws SerializationException {
        if (menuShop != null) {
            node.node("title").set(MiniMessage.miniMessage().serialize(menuShop.title()));
            node.node("structure-outer").set(Arrays.asList(menuShop.outerStructure()));
            node.node("structure-inner").set(Arrays.asList(menuShop.innerStructure()));
            node.node("shop-sells-items").setList(ShopItem.class, menuShop.shopSellsItems());
            node.node("shop-buys-items").setList(ShopItem.class, menuShop.shopBuysItems());
        }
    }
}
