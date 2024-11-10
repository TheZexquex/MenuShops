package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.gui.DefaultValues;
import net.kyori.adventure.text.Component;
import xyz.xenondevs.invui.item.Item;

import java.util.*;

public class MenuShop {
    private String identifier;
    private Component title;
    private final String[] outerStructure;
    private final String[] innerStructure;
    private final LinkedList<ShopItem> shopSellsItems;
    private final LinkedList<ShopItem> shopBuysItems;

    public MenuShop(String identifier, Component title) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = new LinkedList<>();
        this.shopBuysItems = new LinkedList<>();
        this.outerStructure = DefaultValues.STANDARD_STRUCTURE_OUTER;
        this.innerStructure = DefaultValues.STANDARD_STRUCTURE_INNER;
    }

    public MenuShop(String identifier, Component title, String[] outerStructure, String[] innerStructure) {
        this.identifier = identifier;
        this.title = title;
        this.shopSellsItems = new LinkedList<>();
        this.shopBuysItems = new LinkedList<>();
        this.outerStructure = outerStructure;
        this.innerStructure = innerStructure;
    }


    public MenuShop(
            String identifier,
            Component title,
            LinkedList<ShopItem> shopSellsItems,
            LinkedList<ShopItem>  shopBuysItems
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
            LinkedList<ShopItem>  shopSellsItems,
            LinkedList<ShopItem>  shopBuysItems,
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

    public LinkedList<ShopItem>  shopSellsItems() {
        return shopSellsItems;
    }

    public LinkedList<ShopItem>  shopBuysItems() {
        return shopBuysItems;
    }

    public String[] outerStructure() {
        return outerStructure;
    }

    public String[] innerStructure() {
        return innerStructure;
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

    public boolean hasItem(int id, ShopItem.ItemType type) {
        return id >= 0 && type == ShopItem.ItemType.SHOP_BUYS ? id < shopBuysItems.size() : id < shopSellsItems.size();
    }

    public void addItem(ShopItem shopItem, ShopItem.ItemType type) {
        if (type == ShopItem.ItemType.SHOP_SELLS) {
            shopSellsItems.addLast(shopItem);
            return;
        }
        shopBuysItems.addLast(shopItem);
    }

    public boolean removeItem(int id, ShopItem.ItemType type) {
        if (type == ShopItem.ItemType.SHOP_SELLS) {
            return shopSellsItems.remove(id) != null;
        } else {
            return shopBuysItems.remove(id) != null;
        }
    }

    public void editItem(int id, ItemEditInfo itemEditInfo, ShopItem.ItemType type) {
        var shopItem = type == ShopItem.ItemType.SHOP_SELLS ? shopSellsItems.get(id) : shopBuysItems.get(id);

        if (itemEditInfo.newLowerBoundValue() != null) {
            shopItem.lowerBoundValue(itemEditInfo.newLowerBoundValue());
        }
        if (itemEditInfo.newUpperBoundValue() != null) {
            shopItem.upperBoundValue(itemEditInfo.newUpperBoundValue());
        }
        if (itemEditInfo.itemStack() != null) {
            shopItem.itemStack(itemEditInfo.itemStack());
        }

        insertItem(id, shopItem, type, true);
    }

    public void insertItem(int id, ShopItem shopItem, ShopItem.ItemType type, boolean replace) {
        if (type == ShopItem.ItemType.SHOP_SELLS) {
            shopSellsItems.add(id, shopItem);
            if (replace) {
                shopSellsItems.remove(id + 1);
            }
        } else {
            shopBuysItems.add(id, shopItem);
            if (replace) {
                shopBuysItems.remove(id + 1);
            }
        }
    }

    public void swapItems(int firstId, int secondId, ShopItem.ItemType type) {
        ShopItem firstItem;
        ShopItem secondItem;
        if (type == ShopItem.ItemType.SHOP_SELLS) {
            firstItem = shopSellsItems.get(firstId);
            secondItem = shopSellsItems.get(secondId);

            shopSellsItems.set(firstId, secondItem);
            shopSellsItems.set(secondId, firstItem);
        } else {
            firstItem = shopBuysItems.get(firstId);
            secondItem = shopBuysItems.get(secondId);

            shopBuysItems.set(firstId, secondItem);
            shopBuysItems.set(secondId, firstItem);
        }
    }


    public LinkedList<Item> shopSellsGuiItems(Messenger messenger) {
        var guiItems = new LinkedList<Item>();

        shopSellsItems.forEach(shopItem -> guiItems.addLast(shopItem.toSellsItem(messenger, shopSellsItems.indexOf(shopItem), this)));

        return guiItems;
    }

    public LinkedList<Item> shopBuysGuiItems(Messenger messenger) {
        var guiItems = new LinkedList<Item>();
        shopBuysItems.forEach(shopItem -> guiItems.addLast(shopItem.toBuysItem(messenger, shopBuysItems.indexOf(shopItem), this)));

        return guiItems;
    }
}
