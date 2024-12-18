package dev.thezexquex.menushops.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Objects;

public class InventoryUtil {

    public static boolean hasEnoughItems(Player player, ItemStack itemStack, int amount) {
        var possibleItems = Arrays.stream(player.getInventory().getStorageContents())
                .filter(Objects::nonNull)
                .filter(item -> item.isSimilar(itemStack));

        var itemCount = possibleItems.filter(Objects::nonNull).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
        return itemCount >= amount;
    }

    public static void removeSpecificItemCount(Player player, ItemStack itemStack, int amount) {
        var countToRemove = amount;
        var left = amount;
        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (countToRemove == 0) {
                return;
            }
            left = countToRemove;
            if (content != null && content.isSimilar(itemStack)) {
                for (int i = left; i > 0; i--) {
                    if (countToRemove == 0) {
                        return;
                    }
                    countToRemove--;
                    if (content.getAmount() == 1) {
                        player.getInventory().remove(content);
                        break;
                    }
                    content.setAmount(content.getAmount() - 1);
                }
            }
        }
    }

    public static boolean hasNoSpaceInInventory(Player player, ItemStack itemStack, int amount) {
        return getMaxEmptySpaceFor(player, itemStack) < amount;
    }

    public static int getMaxEmptySpaceFor(Player player, ItemStack itemStack) {
        return Arrays.stream(player.getInventory()
                .getStorageContents())
                .mapToInt(item -> item == null ? itemStack.getMaxStackSize() : item.equals(itemStack) ? itemStack.getMaxStackSize() - item.getAmount() : 0)
                .sum();
    }

    public static int getCurrentAmountFor(Player player, ItemStack itemStack) {
        return Arrays.stream(player.getInventory()
                        .getStorageContents())
                .mapToInt(item -> item == null ? 0 : item.isSimilar(itemStack) ? item.getAmount() : 0)
                .sum();
    }
}
