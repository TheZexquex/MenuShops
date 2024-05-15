package dev.thezexquex.menushops.data.shop.typeserializer;

import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class MenuShopTypeSerializer implements TypeSerializer<MenuShop> {

    @Override
    public MenuShop deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var titleString = node.node("title").getString();
        var title = MiniMessage.miniMessage().deserialize(titleString == null ? "<red>N/A title" : titleString);
        var items = node.node("items").getList(ShopItem.class);

        return new MenuShop("", title, items);
    }

    @Override
    public void serialize(Type type, @Nullable MenuShop menuShop, ConfigurationNode node) throws SerializationException {
        if (menuShop != null) {
            node.node("title").set(MiniMessage.miniMessage().serialize(menuShop.title()));
            node.node("items").set(menuShop.items());
        }
    }
}
