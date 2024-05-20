package dev.thezexquex.menushops.shop.gui.item;

import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import dev.thezexquex.menushops.util.InventoryUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
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

public class BuysItem extends AbstractItem {
    private final ItemStack itemStack;
    private final MenuShop menuShop;
    private final Messenger messenger;

    public BuysItem(ItemStack itemStack, MenuShop menuShop, Messenger messenger) {
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
        var shopItem = menuShop.shopBuysItems().get(itemId);


        var currentValue = shopItem.currentValue();

        if (!currentValue.isAvailable(messenger.plugin())) {
            messenger.sendMessage(player, NodePath.path("action", "sell", "currency-unavailable"));
            return;
        }

        if (!InventoryUtil.hasSpaceInInventory(player)) {
            messenger.sendMessage(player, NodePath.path("action", "sell", "inventory-full"));
            return;
        }

        if (!InventoryUtil.hasEnoughItems(player, shopItem.itemStack(), shopItem.itemStack().getAmount())) {
            messenger.sendMessage(player, NodePath.path("action", "sell", "not-enough-items"));
            return;
        }

        currentValue.deposit(player, false);
        messenger.sendMessage(
                player,
                NodePath.path("action", "sell", "success"),
                Placeholder.parsed("amount", String.valueOf(itemStack.getAmount())),
                Placeholder.component("item-name",
                        shopItem.itemStack().getItemMeta().hasDisplayName() ?
                                shopItem.itemStack().displayName() :
                                (shopItem.itemStack().getItemMeta().hasItemName() ? shopItem.itemStack().getItemMeta().itemName() :
                                        Component.text(shopItem.itemStack().getType().name()))
                ),
                Placeholder.component("price", messenger.component(
                        currentValue.formatNode(),
                        Placeholder.parsed("material",
                                (currentValue instanceof MaterialValue materialValue) ?
                                        materialValue.material().name() : ""),
                        Placeholder.parsed("amount", String.valueOf(currentValue.amount()))))
        );

        InventoryUtil.removeSpecificItemCount(player, shopItem.itemStack(), shopItem.itemStack().getAmount());

        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1, 1));
    }
}
