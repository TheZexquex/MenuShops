package dev.thezexquex.menushops.shop;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.gui.item.BuyItem;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

import java.util.List;

public class ShopItem {
    public static final NamespacedKey SHOP_ITEM_ID_KEY = new NamespacedKey("menushops", "shop_item_id");
    private Value upperBoundValue;
    private Value lowerBoundValue;
    private Value currentValue;
    private ItemStack itemStack;

    public ShopItem(ItemStack itemStack, Value upperBoundValue, Value lowerBoundValue) {
        this.itemStack = itemStack;
        this.upperBoundValue = upperBoundValue;
        this.lowerBoundValue = lowerBoundValue;
        // TODO: Implement Value variation mechanic
        this.currentValue = lowerBoundValue;
    }

    public Value upperBoundValue() {
        return upperBoundValue;
    }

    public void upperBoundValue(Value upperBoundValue) {
        this.upperBoundValue = upperBoundValue;
    }

    public Value lowerBoundValue() {
        return lowerBoundValue;
    }

    public void lowerBoundValue(Value lowerBoundValue) {
        this.lowerBoundValue = lowerBoundValue;
    }

    public Value currentValue() {
        return currentValue;
    }

    public void currentValue(Value currentValue) {
        this.currentValue = currentValue;
    }

    public ItemStack itemStack() {
        return itemStack.clone();
    }

    public Item toGuiItem(Messenger messenger, int id, MenuShop menuShop) {
        var modifiedItemStack = itemStack.clone();

        var itemMeta = modifiedItemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(SHOP_ITEM_ID_KEY, PersistentDataType.INTEGER, id);

        modifiedItemStack.setItemMeta(itemMeta);

        var lore = List.of(
                messenger.componentFromList(
                        NodePath.path("gui", "item", "buy", "lore"),
                        TagResolver.resolver(
                                Placeholder.component("price", messenger.component(
                                                currentValue.formatNode(),
                                                TagResolver.resolver(
                                                        Placeholder.parsed("material",
                                                                (currentValue instanceof MaterialValue materialValue) ?
                                                                        materialValue.material().name() : ""),
                                                        Placeholder.parsed("amount", String.valueOf(currentValue.amount()))
                                                )
                                        )
                                )
                        )
                )
        );

        var oldLore = modifiedItemStack.lore();

        if (oldLore == null) {
            modifiedItemStack.lore(lore);
        } else {
            oldLore.addAll(lore);
            modifiedItemStack.lore(oldLore);
        }

        return new BuyItem(modifiedItemStack, menuShop, messenger);
    }
}
