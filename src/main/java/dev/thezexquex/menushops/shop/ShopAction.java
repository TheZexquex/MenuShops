package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ShopAction {
    private ShopItem shopItem;
    private Type type;

    public enum Type {
        CURRENT, STACK, INVENTORY
    }

    public static ShopAction of(ClickType clickType, ShopItem shopItem) {
        if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
            return  new ShopAction(shopItem, ShopAction.Type.STACK);
        } else if (clickType == ClickType.MIDDLE) {
            return new ShopAction(shopItem, ShopAction.Type.INVENTORY);
        }
        return new ShopAction(shopItem, ShopAction.Type.CURRENT);
    }

    public ShopAction(ShopItem shopItem, Type type) {
        this.shopItem = shopItem;
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public ShopItem shopItem() {
        return shopItem;
    }

    public double combinedValue(double originalAmount, Player player) {
        return switch (type) {
            case CURRENT -> originalAmount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    originalAmount / shopItem.itemStack().getAmount() * itemCountSells(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };
    }

    public int itemCountBuys(Player player) {
        return switch (type) {
            case CURRENT -> shopItem.itemStack().getAmount();
            case STACK -> shopItem.itemStack().getMaxStackSize();
            case INVENTORY -> InventoryUtil.getCurrentAmountFor(player, shopItem.itemStack());
        };
    }

    public int itemCountSells(Player player) {
        return switch (type) {
            case CURRENT -> shopItem.itemStack().getAmount();
            case STACK -> shopItem.itemStack().getMaxStackSize();
            case INVENTORY -> InventoryUtil.getMaxEmptySpaceFor(player, shopItem.itemStack());
        };
    }
}
