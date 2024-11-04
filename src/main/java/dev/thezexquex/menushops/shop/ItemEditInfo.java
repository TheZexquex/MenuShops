package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.inventory.ItemStack;

public record ItemEditInfo(
        Value newUpperBoundValue,
        Value newLowerBoundValue,
        ItemStack itemStack
) {
}
