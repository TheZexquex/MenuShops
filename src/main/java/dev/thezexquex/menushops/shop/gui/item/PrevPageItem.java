package dev.thezexquex.menushops.shop.gui.item;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.util.MiniComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class PrevPageItem extends PageItem {
    private final ItemStack displayItem;
    private final Messenger messenger;
    public PrevPageItem(ItemStack displayItem, Messenger messenger) {
        super(true);
        this.displayItem = displayItem;
        this.messenger = messenger;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        var builder = new ItemBuilder(displayItem);
        builder.setDisplayName(new AdventureComponentWrapper(messenger.component(
                NodePath.path("gui", "item", "next-page", "display-name",
                        gui.hasNextPage() ? "has-next" : "no-next"))))
                .addLoreLines(messenger.componentList(NodePath.path("gui", "item", "next-page", "lore",
                        gui.hasNextPage() ? "has-next" : "no-next"))
                        .stream()
                        .map(AdventureComponentWrapper::new)
                        .toArray(AdventureComponentWrapper[]::new)
                );

        return builder;
    }
}