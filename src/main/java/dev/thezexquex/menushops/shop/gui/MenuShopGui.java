package dev.thezexquex.menushops.shop.gui;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.gui.item.ChangeShopModeItem;
import dev.thezexquex.menushops.shop.gui.item.NextPageItem;
import dev.thezexquex.menushops.shop.gui.item.PrevPageItem;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.gui.Markers;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.Structure;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.List;

public class MenuShopGui {
    public static Window constructGui(Player player, MenuShop menuShop, Messenger messenger, HashMap<Character, ItemStack> icons) {
        var outerStructure = new Structure(menuShop.outerStructure());
        var innerSellsStructure = new Structure(menuShop.innerStructure());
        var innerBuysStructure = new Structure(menuShop.innerStructure());

        var nextPageItem = new NextPageItem(icons.get('>'), messenger);
        var prevPageItem = new PrevPageItem(icons.get('<'), messenger);
        var buyBackItem = icons.get('R');
        var sellsTabItem = new ChangeShopModeItem(
                0,
                messenger.component(
                        NodePath.path("gui", "title", "shop-sells"),
                        Placeholder.component("shop-title", menuShop.title())
                ),
                icons.get('S')
        );

        var buysTabItem = new ChangeShopModeItem(
                1,
                messenger.component(
                        NodePath.path("gui", "title", "shop-buys"),
                        Placeholder.component("shop-title", menuShop.title())
                ),
                icons.get('B')
        );


        // The gui that sells items to the player
        var sellGuiBuilder = PagedGui.itemsBuilder().setStructure(innerSellsStructure)
                .addIngredient('>', nextPageItem)
                .addIngredient('<', prevPageItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopSellsGuiItems(messenger));

        icons.forEach((character, itemStack) -> {
            if (isNotReserved(character)) {
                sellGuiBuilder.addIngredient(character, Item.simple(itemStack));
            }
        });

        var sellGui = sellGuiBuilder.build();

        // The gui that buys items from the player
        var buyGuiBuilder = PagedGui.itemsBuilder().setStructure(innerBuysStructure)
                .addIngredient('>', nextPageItem)
                .addIngredient('<', prevPageItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopBuysGuiItems(messenger));

        icons.forEach((character, itemStack) -> {
            if (isNotReserved(character)) {
                buyGuiBuilder.addIngredient(character, Item.simple(itemStack));
            }
        });

        var buyGui = buyGuiBuilder.build();

        // The frame that contains both
        var outerGuiBuilder = TabGui.builder().setStructure(outerStructure)
                .addIngredient('S', sellsTabItem)
                .addIngredient('B', buysTabItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setTabs(List.of(
                        sellGui, buyGui
                ));

        icons.forEach((character, itemStack) -> {
            if (isNotReserved(character)) {
                outerGuiBuilder.addIngredient(character, Item.simple(itemStack));
            }
        });

        var outerGui = outerGuiBuilder.build();


        return Window.builder()
                .setViewer(player)
                .setUpperGui(outerGui)
                .setTitle(messenger.component(
                        NodePath.path("gui", "title", "shop-sells"),
                        Placeholder.component("shop-title", menuShop.title()))
                )
                .build();
    }

    private static boolean isNotReserved(char c) {
        var reservedChars = List.of('<', '>', '.', 'S', 'B', 'R');
        return !reservedChars.contains(c);
    }
}
