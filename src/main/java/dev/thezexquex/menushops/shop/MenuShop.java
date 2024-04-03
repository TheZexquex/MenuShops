package dev.thezexquex.menushops.shop;

import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MenuShop {
    private String identifyer;
    private AdventureComponentWrapper title;
    private final Map<Integer, ShopItem> items;

    public MenuShop(String identifyer, AdventureComponentWrapper title) {
        this.identifyer = identifyer;
        this.title = title;
        this.items = new HashMap<>();
    }


    public MenuShop(String identifyer, AdventureComponentWrapper title, Map<Integer, ShopItem> items) {
        this.identifyer = identifyer;
        this.title = title;
        this.items = items;
    }

    public String identifyer() {
        return identifyer;
    }

    public AdventureComponentWrapper title() {
        return title;
    }

    public Map<Integer, ShopItem> items() {
        return items;
    }

    public MenuShop identifyer(String identifyer) {
        this.identifyer = identifyer;
        return this;
    }

    public MenuShop title(AdventureComponentWrapper title) {
        this.title = title;
        return this;
    }

    public void addItem(ShopItem shopItem) {
        var newId = items.keySet().stream().max(Comparator.comparingInt(value -> value)).orElse(-1) + 1;
        items.put(newId, shopItem);
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

        items.put(id, shopItem);
    }
}
