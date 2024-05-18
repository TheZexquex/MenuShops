package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.gui.DefaultValues;
import net.kyori.adventure.text.Component;
import xyz.xenondevs.invui.item.Item;

import java.util.*;

public class MenuShop {
    private String identifier;
    private Component title;
    private String[] outerStructure;
    private String[] innerStructure;
    private final HashMap<Integer, ShopItem> shopSellsItems;
    private final HashMap<Integer, ShopItem> shopBuysItems;

    public MenuShop(String identifier, Component title) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = new HashMap<>();
        this.shopBuysItems = new HashMap<>();
        this.outerStructure = DefaultValues.STANDARD_STRUCTURE_OUTER;
        this.innerStructure = DefaultValues.STANDARD_STRUCTURE_INNER;
    }

    public MenuShop(String identifier, Component title, String[] outerStructure, String[] innerStructure) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = new HashMap<>();
        this.shopBuysItems = new HashMap<>();
        this.outerStructure = outerStructure;
        this.innerStructure = innerStructure;
    }


    public MenuShop(
            String identifier,
            Component title,
            HashMap<Integer, ShopItem> shopSellsItems,
            HashMap<Integer, ShopItem> shopBuysItems
    ) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = shopSellsItems;
        this.shopBuysItems = shopBuysItems;
        this.outerStructure = DefaultValues.STANDARD_STRUCTURE_OUTER;
        this.innerStructure = DefaultValues.STANDARD_STRUCTURE_INNER;
    }

    public MenuShop(
            String identifier,
            Component title,
            HashMap<Integer, ShopItem> shopSellsItems,
            HashMap<Integer, ShopItem> shopBuysItems,
            String[] outerStructure,
            String[] innerStructure
    ) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = shopSellsItems;
        this.shopBuysItems = shopBuysItems;
        this.outerStructure = outerStructure;
        this.innerStructure = innerStructure;
    }

    public HashMap<Integer, ShopItem> shopSellsItems() {
        return shopSellsItems;
    }

    public HashMap<Integer, ShopItem> shopBuysItems() {
        return shopBuysItems;
    }

    public String[] outerStructure() {
        return outerStructure;
    }

    public void outerStructure(String[] outerStructure) {
        this.outerStructure = outerStructure;
    }

    public String[] innerStructure() {
        return innerStructure;
    }

    public void innerStructure(String[] innerStructure) {
        this.innerStructure = innerStructure;
    }

    public String identifier() {
        return identifier;
    }

    public Component title() {
        return title;
    }

    public void identifier(String identifier) {
        this.identifier = identifier;
    }

    public void title(Component title) {
        this.title = title;
    }

    public void addSellsItem(ShopItem shopItem) {
        shopSellsItems.put(shopSellsItems.values().size(), shopItem);
    }

    public void addBuysItem(ShopItem shopItem) {
        shopBuysItems.put(shopBuysItems.values().size(), shopItem);
    }

    public boolean removeSellsItem(int id) {
        var exists = shopSellsItems.containsKey(id);
        shopSellsItems.remove(id);
        return exists;
    }

    public boolean removeBuysItem(int id) {
        var exists = shopBuysItems.containsKey(id);
        shopBuysItems.remove(id);
        return exists;
    }

    public void editSellsItem(int id, ItemEditInfo itemEditInfo) {
        var shopItem = shopSellsItems.get(id);

        if (itemEditInfo.newLowerBoundValue() != null) {
            shopItem.lowerBoundValue(itemEditInfo.newLowerBoundValue());
        }
        if (itemEditInfo.newUpperBoundValue() != null) {
            shopItem.upperBoundValue(itemEditInfo.newUpperBoundValue());
        }

        shopSellsItems.put(id, shopItem);
    }

    public HashMap<Integer, Item> shopSellsGuiItems(Messenger messenger) {
        var guiItems = new HashMap<Integer, Item>();
        shopSellsItems.forEach((integer, shopItem) -> guiItems.put(integer, shopItem.toSellsItem(messenger, integer, this)));

        return guiItems;
    }

    public HashMap<Integer, Item> shopBuysGuiItems(Messenger messenger) {
        var guiItems = new HashMap<Integer, Item>();
        shopBuysItems.forEach((integer, shopItem) -> guiItems.put(integer, shopItem.toBuysItem(messenger, integer, this)));

        return guiItems;
    }
}
