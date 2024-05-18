package dev.thezexquex.menushops.shop.gui;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.gui.item.BackItem;
import dev.thezexquex.menushops.shop.gui.item.ChangeShopModeItem;
import dev.thezexquex.menushops.shop.gui.item.ForwardItem;
import dev.thezexquex.menushops.util.MiniComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
import java.util.List;
import java.util.stream.Collectors;

public class MenuShopGui {
    public static Window constructGui(Player player, MenuShop menuShop, Messenger messenger) {
        List<Item> items = Arrays.stream(Material.values())
                .filter(material -> !material.isAir() && material.isItem())
                .map(material -> new SimpleItem(new ItemBuilder(material)))
                .collect(Collectors.toList());


        var outerStructure = new Structure(menuShop.outerStructure());
        var innerSellsStructure = new Structure(menuShop.innerStructure());
        var innerBuysStructure = new Structure(menuShop.innerStructure());

        var outlineItem = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("");
        var forwardItem = new ForwardItem();
        var backItem = new BackItem();
        var buyBackItem = new ItemBuilder(Material.HOPPER).setDisplayName(MiniComponent.of("<gray>Noch kein Item verfügbar"));
        var sellsTabItem = new ChangeShopModeItem(
                0,
                messenger.component(
                        NodePath.path("gui", "title", "shop-sells"),
                        Placeholder.component("shop-title", menuShop.title())
                ),
                new ItemStack(Material.IRON_INGOT)
        );

        var buysTabItem = new ChangeShopModeItem(
                1,
                messenger.component(
                        NodePath.path("gui", "title", "shop-buys"),
                        Placeholder.component("shop-title", menuShop.title())
                ),
                new ItemStack(Material.GOLD_INGOT)
        );


        // The gui that sells items to the player
        var sellGui = PagedGui.items().setStructure(innerSellsStructure)
                .addIngredient('#', outlineItem)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopSellsGuiItems(messenger).values().stream().toList())
                .build();

        // The gui that buys items from the player
        var buyGui = PagedGui.items().setStructure(innerBuysStructure)
                .addIngredient('#', outlineItem)
                .addIngredient('<', backItem)
                .addIngredient('>', forwardItem)
                .addIngredient('R', buyBackItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .setContent(menuShop.shopBuysGuiItems(messenger).values().stream().toList())
                .build();

        // The frame that contains both
        var outerGui = TabGui.normal().setStructure(outerStructure)
                .addIngredient('#', outlineItem)
                .addIngredient('S', sellsTabItem)
                .addIngredient('B', buysTabItem)
                .addIngredient('.', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addTab(sellGui)
                .addTab(buyGui)
                .build();


        return Window.single()
                .setViewer(player)
                .setGui(outerGui)
                .setTitle(new AdventureComponentWrapper(messenger.component(
                        NodePath.path("gui", "title", "shop-sells"),
                        Placeholder.component("shop-title", menuShop.title()))
                ))
                .build();
    }
}
