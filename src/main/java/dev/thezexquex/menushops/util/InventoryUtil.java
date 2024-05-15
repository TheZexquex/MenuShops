package dev.thezexquex.menushops.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class InventoryUtil {

    public static boolean hasEnoughItems(Player player, ItemStack itemStack, int amount) {
        var possibleItems = Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(item -> item.isSimilar(itemStack));

        var itemCount = possibleItems.filter(Objects::nonNull).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
        return itemCount >= amount;
    }

    public static void removeSpecificItemCount(Player player, ItemStack itemStack, int amount) {
        var countToRemove = amount;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.isSimilar(itemStack)) {
                if (countToRemove == 0) {
                    return;
                }
                if (countToRemove >= content.getAmount()) {
                    countToRemove -= content.getAmount();
                    player.getInventory().remove(itemStack);
                    continue;
                }
                content.setAmount(content.getAmount() - countToRemove);
            }
        }
    }

    public static boolean hasSpaceInInventory(Player player) {
        return Arrays.stream(player.getInventory().getContents()).noneMatch(Objects::isNull);
    }
}
