package dev.thezexquex.menushops.data.shop.typeserializer;

import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.gui.DefaultValues;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

public class MenuShopTypeSerializer implements TypeSerializer<MenuShop> {

    @Override
    public MenuShop deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var titleString = node.node("title").getString();
        var title = MiniMessage.miniMessage().deserialize(titleString == null ? "<red>N/A title" : titleString);
        var structureList = node.node("structure").getList(String.class);

        var itemNodes = node.node("items").childrenMap();

        var items = new HashMap<Integer, ShopItem>();
        for (Object key : itemNodes.keySet()) {
            items.put(Integer.parseInt(String.valueOf(key)), itemNodes.get(key).get(ShopItem.class));
        }

        return new MenuShop(
                "",
                title,
                items,
                (structureList == null) || (structureList.isEmpty()) ?
                        DefaultValues.STANDARD_STRUCTURE :
                        structureList.toArray(String[]::new)
        );
    }

    @Override
    public void serialize(Type type, @Nullable MenuShop menuShop, ConfigurationNode node) throws SerializationException {
        if (menuShop != null) {
            node.node("title").set(MiniMessage.miniMessage().serialize(menuShop.title()));
            node.node("structure").set(Arrays.asList(menuShop.structure()));
            for (Integer key : menuShop.items().keySet()) {
                node.node("items").node(String.valueOf(key)).set(ShopItem.class, menuShop.items().get(key));
            }
        }
    }
}
