package dev.thezexquex.menushops.shop.gui.item;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.ItemWrapper;
import xyz.xenondevs.invui.item.impl.controlitem.TabItem;

public class ChangeShopModeItem extends TabItem {
    private final int tab;
    private final Component tabTitle;
    private final ItemStack tabDisplayItem;
    public ChangeShopModeItem(int tab, Component tabTitle, ItemStack tabDisplayItem) {
        super(tab);
        this.tab = tab;
        this.tabTitle = tabTitle;
        this.tabDisplayItem = tabDisplayItem;
    }

    @Override
    public ItemProvider getItemProvider(TabGui tabGui) {
        var activeTabItemStack = tabDisplayItem.clone();
        var activeTabItemStackMeta = activeTabItemStack.getItemMeta();
        activeTabItemStackMeta.setEnchantmentGlintOverride(true);
        activeTabItemStack.setItemMeta(activeTabItemStackMeta);

        var activeTabItem = new ItemWrapper(activeTabItemStack);
        var inactiveTabItem = new ItemWrapper(tabDisplayItem);

        if (tabGui.getCurrentTab() == tab) {
            return activeTabItem;
        } else {
            return inactiveTabItem;
        }
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        super.handleClick(clickType, player, event);
        var window = getWindows().stream().filter(w -> w.getCurrentViewer().equals(player)).findFirst().get();

        window.changeTitle(new AdventureComponentWrapper(tabTitle));
    }
}
