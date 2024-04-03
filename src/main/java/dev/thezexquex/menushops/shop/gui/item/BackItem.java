package dev.thezexquex.menushops.shop.gui.item;

import dev.thezexquex.menushops.utils.MiniComponent;
import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class BackItem extends PageItem {
    public BackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        var builder = new ItemBuilder(Material.PAINTING);
        builder.setDisplayName(MiniComponent.of("<!i><gradient:#fc9403:#fce803>Vorherige Seite</gradient>"))
                .addLoreLines(gui.hasPreviousPage() ?
                        MiniComponent.of("<gray><!i>Klicke um auf Seite <dark_gray><!i>(<yellow>" + (gui.getCurrentPage()) + "<dark_gray>/ <yellow>" + gui.getPageAmount() + "<dark_gray>)<!i> <gray>zu kommen")
                        : MiniComponent.of("<red>Es gibt keine vorherigen Seiten")
                );

        return builder;
    }
}
