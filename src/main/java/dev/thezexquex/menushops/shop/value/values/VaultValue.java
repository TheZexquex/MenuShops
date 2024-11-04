package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.externalhooks.VaultHook;
import dev.thezexquex.menushops.shop.ShopAction;
import dev.thezexquex.menushops.shop.value.Amount;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.util.InventoryUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class VaultValue extends Value {

    public VaultValue(double amount) {
        super(amount);
    }

    @Override
    public void withdraw(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValue(amount, player);

        plugin.vaultEconomy().withdrawPlayer(player, actualAmount);
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = switch (shopAction.type()) {
            case CURRENT -> amount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    amount / shopAction.shopItem().itemStack().getAmount() * shopAction.itemCountBuys(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };

        plugin.vaultEconomy().depositPlayer(player, actualAmount);
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = switch (shopAction.type()) {
            case CURRENT -> amount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    amount / shopAction.shopItem().itemStack().getAmount() * shopAction.itemCountSells(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };

        return plugin.vaultEconomy().has(player, actualAmount);
    }

    @Override
    public boolean isAvailable(MenuShopsPlugin plugin) {
        return plugin.pluginHookService().isAvailable(VaultHook.class) && plugin.vaultEconomy() != null;
    }

    @Override
    public NodePath formatNode() {
        return NodePath.path("gui", "value-format", "vault-money");
    }
}