package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.gui.DefaultValues;
import net.kyori.adventure.text.Component;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import xyz.xenondevs.invui.item.Item;

import java.util.*;

public class MenuShop {
    private String identifier;
    private Component title;
    private String[] structure;
    private final HashMap<Integer, ShopItem> items;

    public MenuShop(String identifier, Component title) {
        this.identifier = identifier;
        this.title = title;
        this.items = new HashMap<>();
        this.structure = DefaultValues.STANDARD_STRUCTURE;
    }

    public MenuShop(String identifier, Component title, String[] structure) {
        this.identifier = identifier;
        this.title = title;
        this.items = new HashMap<>();
        this.structure = structure;
    }


    public MenuShop(String identifier, Component title, HashMap<Integer, ShopItem> items) {
        this.identifier = identifier;
        this.title = title;
        this.items = items;
        this.structure = DefaultValues.STANDARD_STRUCTURE;
    }

    public MenuShop(String identifier, Component title, HashMap<Integer, ShopItem> items, String[] structure) {
        this.identifier = identifier;
        this.title = title;
        this.items = items;
        this.structure = structure;
    }

    public void structure(String[] structure) {
        this.structure = structure;
    }

    public String[] structure() {
        return structure;
    }

    public String identifier() {
        return identifier;
    }

    public Component title() {
        return title;
    }

    public HashMap<Integer, ShopItem> items() {
        return items;
    }

    public void identifier(String identifier) {
        this.identifier = identifier;
    }

    public void title(Component title) {
        this.title = title;
    }

    public void addItem(ShopItem shopItem) {
        items.put(items.values().size(), shopItem);
    }

    public boolean removeItem(int id) {
        var exists = items.containsKey(id);
        items.remove(id);
        return exists;
    }

    public void editItem(int id, ItemEditInfo itemEditInfo) {
        var shopItem = items.get(id);

        if (itemEditInfo.newLowerBoundValue() != null) {
            shopItem.lowerBoundValue(itemEditInfo.newLowerBoundValue());
        }
        if (itemEditInfo.newUpperBoundValue() != null) {
            shopItem.upperBoundValue(itemEditInfo.newUpperBoundValue());
        }

        items.put(id, shopItem);
    }

    public HashMap<Integer, Item> guiItems(Messenger messenger) {
        var guiItems = new HashMap<Integer, Item>();
        items.forEach((integer, shopItem) -> guiItems.put(integer, shopItem.toGuiItem(messenger, integer, this)));

        return guiItems;
    }
}
