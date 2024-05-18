package dev.thezexquex.menushops.shop.gui.item;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import dev.thezexquex.menushops.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class SellsItem extends AbstractItem {
    private final ItemStack itemStack;
    private final MenuShop menuShop;
    private final Messenger messenger;

    public SellsItem(ItemStack itemStack, MenuShop menuShop, Messenger messenger) {
        this.itemStack = itemStack;
        this.menuShop = menuShop;
        this.messenger = messenger;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent inventoryClickEvent) {
        var itemId = itemStack.getItemMeta().getPersistentDataContainer().get(ShopItem.SHOP_ITEM_ID_KEY, PersistentDataType.INTEGER);
        var shopItem = menuShop.shopSellsItems().get(itemId);

        if (!InventoryUtil.hasSpaceInInventory(player)) {
            messenger.sendMessage(player, NodePath.path("action", "buy", "inventory-full"));
            return;
        }

        var currentValue = shopItem.currentValue();

        if (!currentValue.hasEnough(player, false)) {
            messenger.sendMessage(player, NodePath.path("action", "buy", "price-too-high"));
            return;
        }

        currentValue.withdraw(player, false);
        messenger.sendMessage(
                player,
                NodePath.path("action", "buy", "success"),
                TagResolver.resolver(
                        Placeholder.parsed("amount", String.valueOf(itemStack.getAmount())),
                        Placeholder.component("item-name",
                                shopItem.itemStack().getItemMeta().hasDisplayName() ?
                                        shopItem.itemStack().displayName() :
                                        (shopItem.itemStack().getItemMeta().hasItemName() ? shopItem.itemStack().getItemMeta().itemName() : Component.text(itemStack.getType().name()))
                        ),
                        Placeholder.component("price", messenger.component(
                        currentValue.formatNode(),
                        TagResolver.resolver(
                                Placeholder.parsed("material",
                                        (currentValue instanceof MaterialValue materialValue) ?
                                                materialValue.material().name() : ""),
                                Placeholder.parsed("amount", String.valueOf(currentValue.amount()))
                        ))))
        );

        player.getInventory().addItem(shopItem.itemStack());
    }
}
