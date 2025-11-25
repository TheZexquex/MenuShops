package dev.thezexquex.menushops.shop.gui.item;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.message.Messenger;
import dev.thezexquex.menushops.shop.MenuShop;
import dev.thezexquex.menushops.shop.ShopAction;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.NodePath;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.item.AbstractBoundItem;
import xyz.xenondevs.invui.item.ItemBuilder;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Arrays;
import java.util.logging.Level;

public class ShopSellsItem extends AbstractBoundItem {
    private final ItemStack itemStack;
    private final MenuShop menuShop;
    private final Messenger messenger;

    public ShopSellsItem(ItemStack itemStack, MenuShop menuShop, Messenger messenger) {
        this.itemStack = itemStack;
        this.menuShop = menuShop;
        this.messenger = messenger;
    }

    @Override
    public @NotNull ItemProvider getItemProvider(@NotNull Player player) {
        return new ItemBuilder(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
        var itemId = itemStack.getItemMeta().getPersistentDataContainer().get(ShopItem.SHOP_ITEM_ID_KEY, PersistentDataType.INTEGER);

        if (itemId == null) {
            messenger.plugin().getLogger().log(Level.SEVERE, "Shop contains unmapped Item. (%s) This should never happen, please contact the plugin developer".formatted(itemStack.displayName()));
            return;
        }

        var shopItem = menuShop.shopSellsItems().get(itemId);

        var shopAction = ShopAction.of(clickType, shopItem);

        var currentValue = shopItem.currentValue();

        if (!currentValue.isAvailable(messenger.plugin())) {
            messenger.sendMessage(player, NodePath.path("action", "buy", "currency-unavailable"));
            return;
        }

        if (InventoryUtil.hasNoSpaceInInventory(player, itemStack, shopAction.itemCountSells(player, JavaPlugin.getPlugin(MenuShopsPlugin.class)))) {
            messenger.sendMessage(player, NodePath.path("action", "buy", "inventory-full"));
            return;
        }

        if (!currentValue.hasEnough(player, messenger.plugin(), shopAction) || shopAction.itemCountSells(player, JavaPlugin.getPlugin(MenuShopsPlugin.class)) < 1) {
            messenger.sendMessage(player, NodePath.path("action", "buy", "price-too-high"));
            return;
        }

        messenger.sendMessage(
                player,
                NodePath.path("action", "buy", "success"),
                TagResolver.resolver(
                        Placeholder.parsed("amount", String.valueOf(shopAction.itemCountSells(player, JavaPlugin.getPlugin(MenuShopsPlugin.class)))),
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
                                Placeholder.parsed("amount", String.valueOf(shopAction.combinedValueSells(currentValue.amount(), player, JavaPlugin.getPlugin(MenuShopsPlugin.class))))
                        ))))
        );

        switch (shopAction.type()) {
            case CURRENT -> player.getInventory().addItem(shopItem.itemStack());
            case STACK, INVENTORY -> {
                var itemToAdd = shopItem.itemStack();
                itemToAdd.setAmount(shopAction.itemCountSells(player, JavaPlugin.getPlugin(MenuShopsPlugin.class)));
                player.getInventory().addItem(itemToAdd);
            }
        }

        currentValue.withdraw(player, messenger.plugin(), shopAction);

        player.playSound(Sound.sound(Key.key("entity.experience_orb.pickup"), Sound.Source.MASTER, 1, 1));
    }
}
