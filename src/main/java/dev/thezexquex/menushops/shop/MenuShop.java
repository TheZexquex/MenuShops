package dev.thezexquex.menushops.shop;

import net.kyori.adventure.text.Component;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.item.Item;

import java.util.*;

public class MenuShop {
    private String identifier;
    private Component title;
    private final List<ShopItem> items;

    public MenuShop(String identifier, Component title) {
        this.identifier = identifier;
        this.title = title;
        this.items = new ArrayList<>();
    }


    public MenuShop(String identifier, Component title, List<ShopItem> items) {
        this.identifier = identifier;
        this.title = title;
        this.items = items;
    }

    public String identifier() {
        return identifier;
    }

    public Component title() {
        return title;
    }

    public List<ShopItem> items() {
        return items;
    }

    public MenuShop identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public MenuShop title(Component title) {
        this.title = title;
        return this;
    }

    public void addItem(ShopItem shopItem) {
        items.add(shopItem);
    }

    public void removeItem(int id) {
        items.remove(id);
    }

    public void editItem(int id, ItemEditInfo itemEditInfo) {
        var shopItem = items.get(id);

        if (itemEditInfo.newLowerBoundValue() != null) {
            shopItem.lowerBoundValue(itemEditInfo.newLowerBoundValue());
        }
        if (itemEditInfo.newUpperBoundValue() != null) {
            shopItem.upperBoundValue(itemEditInfo.newUpperBoundValue());
        }

        items.add(shopItem);
    }

    public List<Item> guiItems() {
        return items.stream().map(ShopItem::toGuiItem).toList();
    }
}
