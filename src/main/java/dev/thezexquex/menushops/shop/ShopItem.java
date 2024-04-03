package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.inventory.ItemStack;

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
}
