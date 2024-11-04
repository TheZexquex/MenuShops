package dev.thezexquex.menushops.shop.gui;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.gui.item.NextPageItem;
import dev.thezexquex.menushops.shop.gui.item.ChangeShopModeItem;
import dev.thezexquex.menushops.shop.gui.item.PrevPageItem;
import dev.thezexquex.menushops.util.MiniComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.TabGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MenuShopGui {
    public static Window constructGui(Player player, MenuShop menuShop, Messenger messenger, HashMap<Character, ItemStack> icons) {
        var outerStructure = new Structure(menuShop.outerStructure());
        var innerSellsStructure = new Structure(menuShop.innerStructure());
        var innerBuysStructure = new Structure(menuShop.innerStructure());

        var forwardItem = new PrevPageItem(icons.get('>'), messenger);
        var backItem = new NextPageItem(icons.get('>'), messenger);
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
        var sellGuiBuilder = PagedGui.items().setStructure(innerSellsStructure)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopSellsGuiItems(messenger));

        icons.forEach((character, itemStack) -> {
            if (!isReserved(character)) {
                sellGuiBuilder.addIngredient(character, new SimpleItem(itemStack));
            }
        });

        var sellGui = sellGuiBuilder.build();

        // The gui that buys items from the player
        var buyGuiBuilder = PagedGui.items().setStructure(innerBuysStructure)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopBuysGuiItems(messenger));

        icons.forEach((character, itemStack) -> {
            if (!isReserved(character)) {
                buyGuiBuilder.addIngredient(character, new SimpleItem(itemStack));
            }
        });

        var buyGui = buyGuiBuilder.build();

        // The frame that contains both
        var outerGuiBuilder = TabGui.normal().setStructure(outerStructure)
                .addIngredient('S', sellsTabItem)
                .addIngredient('B', buysTabItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addTab(sellGui)
                .addTab(buyGui);

        icons.forEach((character, itemStack) -> {
            if (!isReserved(character)) {
                outerGuiBuilder.addIngredient(character, new SimpleItem(itemStack));
            }
        });

        var outerGui = outerGuiBuilder.build();


        return Window.single()
                .setViewer(player)
                .setGui(outerGui)
                .setTitle(new AdventureComponentWrapper(messenger.component(
                        NodePath.path("gui", "title", "shop-sells"),
                        Placeholder.component("shop-title", menuShop.title()))
                ))
                .build();
    }

    private static boolean isReserved(char c) {
        var reservedChars = List.of('<', '>', '.', 'S', 'B', 'R');
        return reservedChars.contains(c);
    }

}
