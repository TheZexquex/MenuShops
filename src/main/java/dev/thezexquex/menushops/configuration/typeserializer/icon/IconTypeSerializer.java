package dev.thezexquex.menushops.configuration.typeserializer.icon;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class IconTypeSerializer implements TypeSerializer<ItemStack> {

    @Override
    public ItemStack deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        var material = Material.valueOf(node.node("material").getString());
        var amount = node.node("amount").getInt(1);

        var itemStack = new ItemStack(material, amount);

        var loreStrings = node.node("lore").getList(String.class);

        if (loreStrings != null && !loreStrings.isEmpty()) {
            var lore = loreStrings.stream().map(string -> MiniMessage.miniMessage().deserialize(string)).toList();
            itemStack.lore(lore);
        }

        var itemMeta = itemStack.getItemMeta();

        var displayName = node.node("display-name").getString();
        if (displayName != null) {
            itemMeta.displayName(MiniMessage.miniMessage().deserialize(displayName));
        }

        if (node.hasChild("custom-model-data")) {
            var customModelData = node.node("custom-model-data").getInt();

            itemMeta.setCustomModelData(customModelData);
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Override
    public void serialize(@NotNull Type type, @Nullable ItemStack obj, @NotNull ConfigurationNode node) {

    }
}
