package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.util.InventoryUtil;
import dev.thezexquex.menushops.util.ShopUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ShopAction(ShopItem shopItem, ShopAction.Type type) {
    public enum Type {
        CURRENT, STACK, INVENTORY
    }

    public static ShopAction of(ClickType clickType, ShopItem shopItem) {
        if (clickType == ClickType.SHIFT_LEFT) {
            return new ShopAction(shopItem, ShopAction.Type.STACK);
        } else if (clickType == ClickType.SHIFT_RIGHT) {
            return new ShopAction(shopItem, ShopAction.Type.INVENTORY);
        }
        return new ShopAction(shopItem, ShopAction.Type.CURRENT);
    }

    public double combinedValueBuys(double originalAmount, Player player) {
        return switch (type) {
            case CURRENT -> originalAmount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    originalAmount / shopItem.itemStack().getAmount() * itemCountBuys(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };
    }

    public double combinedValueSells(double originalAmount, Player player, MenuShopsPlugin plugin) {
        return switch (type) {
            case CURRENT -> originalAmount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    originalAmount / shopItem.itemStack().getAmount() * itemCountSells(player, plugin)
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

    public int itemCountSells(Player player, MenuShopsPlugin plugin) {
        return switch (type) {
            case CURRENT -> shopItem.itemStack().getAmount();
            case STACK -> ShopUtil.getMaxBuyAmountStack(shopItem, player, plugin, shopItem.itemStack().getMaxStackSize());
            case INVENTORY -> ShopUtil.getMaxBuyAmount(shopItem, player, plugin);
        };
    }
}
