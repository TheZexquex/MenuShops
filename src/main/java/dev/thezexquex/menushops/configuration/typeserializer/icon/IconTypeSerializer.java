package dev.thezexquex.menushops.configuration.typeserializer.icon;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class IconTypeSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var material = Material.valueOf(node.node("material").getString());
        var amount = node.node("amount").getInt(1);

        var itemStack = new ItemStack(material, amount);

        var loreStrings = node.node("lore").getList(String.class);

        if (loreStrings != null && !loreStrings.isEmpty()) {
            var lore = loreStrings.stream().map(string -> MiniMessage.miniMessage().deserialize(string)).toList();
            itemStack.lore(lore);
        }

        var displayName = node.node("display-name").getString();
        if (displayName != null) {
            var itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayName));

            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {

    }
}
