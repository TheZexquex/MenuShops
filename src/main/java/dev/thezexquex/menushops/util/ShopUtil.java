package dev.thezexquex.menushops.util;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.shop.ShopItem;
import org.bukkit.entity.Player;

public class ShopUtil {

    public static int getMaxBuyAmount(ShopItem shopItem, Player player, MenuShopsPlugin plugin) {
        var maxAmountCanAfford = shopItem.currentValue().maxAmountCanAfford(player, plugin);
        return Math.min(InventoryUtil.getMaxEmptySpaceFor(player, shopItem.itemStack()), maxAmountCanAfford);
    }

    public static int getMaxBuyAmountStack(ShopItem shopItem, Player player, MenuShopsPlugin plugin, int maxStackSize) {
        return Math.min(maxStackSize, shopItem.currentValue().maxAmountCanAfford(player, plugin));
    }
}
