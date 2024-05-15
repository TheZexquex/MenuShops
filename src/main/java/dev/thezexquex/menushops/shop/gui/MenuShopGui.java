package dev.thezexquex.menushops.shop.gui;

import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.gui.item.BackItem;
import dev.thezexquex.menushops.shop.gui.item.ForwardItem;
import dev.thezexquex.menushops.util.MiniComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MenuShopGui {
    public static Window constructGui(Player player, MenuShop menuShop) {
        List<Item> items = Arrays.stream(Material.values())
                .filter(material -> !material.isAir() && material.isItem())
                .map(material -> new SimpleItem(new ItemBuilder(material)))
                .collect(Collectors.toList());


        var structure = new Structure(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # < # B # > # #"
        );

        var outlineItem = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("");
        var forwardItem = new ForwardItem();
        var backItem = new BackItem();
        var buyBackItem = new ItemBuilder(Material.HOPPER).setDisplayName(MiniComponent.of("<gray>Noch kein Item verfügbar"));

        var gui = PagedGui.items().setStructure(structure)
                .addIngredient('#', outlineItem)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .addIngredient('B', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.guiItems())
                .build();

        return Window.single()
                .setViewer(player)
                .setGui(gui)
                .setTitle(new AdventureComponentWrapper(menuShop.title()))
                .build();
    }
}
