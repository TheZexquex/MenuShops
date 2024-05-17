package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.value.Value;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;

public class ShopItem {
    private Value upperBoundValue;
    private Value lowerBoundValue;
    private Value currentValue;
    private ItemStack itemStack;

    public ShopItem(ItemStack itemStack, Value upperBoundValue, Value lowerBoundValue) {
        this.itemStack = itemStack;
        this.upperBoundValue = upperBoundValue;
        this.lowerBoundValue = lowerBoundValue;
        // TODO: Implement Value variation mechanic
        this.currentValue = lowerBoundValue;
    }

    public Value upperBoundValue() {
        return upperBoundValue;
    }

    public void upperBoundValue(Value upperBoundValue) {
        this.upperBoundValue = upperBoundValue;
    }

    public Value lowerBoundValue() {
        return lowerBoundValue;
    }

    public void lowerBoundValue(Value lowerBoundValue) {
        this.lowerBoundValue = lowerBoundValue;
    }

    public ItemStack itemStack() {
        return itemStack;
    }

    public Item toGuiItem(Messenger messenger) {
        var modifiedItemStack = itemStack.clone();
        var lore = List.of(
                messenger.componentFromList(
                        NodePath.path("gui", "item", "buy", "lore"),
                        TagResolver.resolver(
                                Placeholder.component("price", messenger.component(currentValue.formatNode()))
                        )
                )
        );

        var oldLore = modifiedItemStack.lore();

        if (oldLore == null) {
            modifiedItemStack.lore(lore);
        } else {
            oldLore.addAll(lore);
            modifiedItemStack.lore(oldLore);
        }

        return new SimpleItem(modifiedItemStack);
    }
}
