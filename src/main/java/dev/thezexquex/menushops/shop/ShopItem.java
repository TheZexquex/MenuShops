package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;

public class ShopItem {
    private Value upperBoundValue;
    private Value lowerBoundValue;
    private ItemStack itemStack;

    public ShopItem(ItemStack itemStack, Value upperBoundValue, Value lowerBoundValue) {
        this.itemStack = itemStack;
        this.upperBoundValue = upperBoundValue;
        this.lowerBoundValue = lowerBoundValue;
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

    public Item toGuiItem() {
        var modifiedItemStack = itemStack.clone();
        var lore = List.of(
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<r><gold>Preis: <gray>" + ValueParser.toPattern(lowerBoundValue))
        );

        if (modifiedItemStack.lore() == null) {
            modifiedItemStack.lore(lore);
        } else {
            var temLore = modifiedItemStack.lore();
            temLore.addAll(lore);
            modifiedItemStack.lore(temLore);
        }

        return new SimpleItem(modifiedItemStack);
    }
}
