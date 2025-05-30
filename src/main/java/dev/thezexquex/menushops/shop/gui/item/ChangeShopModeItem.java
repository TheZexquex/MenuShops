package dev.thezexquex.menushops.shop.gui.item;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.item.AbstractTabGuiBoundItem;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;

public class ChangeShopModeItem extends AbstractTabGuiBoundItem {
    private final int tab;
    private final Component tabTitle;
    private final ItemStack tabDisplayItem;
    public ChangeShopModeItem(int tab, Component tabTitle, ItemStack tabDisplayItem) {
        this.tab = tab;
        this.tabTitle = tabTitle;
        this.tabDisplayItem = tabDisplayItem;
    }

    @Override
    public @NotNull ItemProvider getItemProvider(@NotNull Player player) {
        var activeTabItemStack = tabDisplayItem.clone();
        var activeTabItemStackMeta = activeTabItemStack.getItemMeta();
        activeTabItemStackMeta.setEnchantmentGlintOverride(true);
        activeTabItemStack.setItemMeta(activeTabItemStackMeta);

        var activeTabItem = new ItemWrapper(activeTabItemStack);
        var inactiveTabItem = new ItemWrapper(tabDisplayItem);

        if (getGui().getTab() == tab) {
            return activeTabItem;
        } else {
            return inactiveTabItem;
        }
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
        getGui().setTab(tab);
        var windowOpt = getGui().getWindows().stream().filter(w -> w.getViewer().equals(player)).findFirst();

        windowOpt.ifPresent(window -> window.setTitle(tabTitle));
    }
}