package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.Hook;
import dev.thezexquex.menushops.hooks.externalhooks.CoinsEngineHook;
import dev.thezexquex.menushops.shop.ShopAction;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoinsEngineValue extends Value {
    private final String currency;
    public CoinsEngineValue(double amount, String currency) {
        super(amount);
        this.currency = currency;
    }

    @Override
    public void withdraw(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValue(amount, player);

        var curr = CoinsEngineAPI.getCurrency(currency);
        if (curr != null) {
            CoinsEngineAPI.removeBalance(player, curr, actualAmount);
        }
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var curr = CoinsEngineAPI.getCurrency(currency);

        var actualAmount = switch (shopAction.type()) {
            case CURRENT -> amount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    amount / shopAction.shopItem().itemStack().getAmount() * shopAction.itemCountBuys(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };

        if (curr != null) {
            CoinsEngineAPI.addBalance(player, curr, actualAmount);
        }
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = switch (shopAction.type()) {
            case CURRENT -> amount;
            case STACK, INVENTORY -> BigDecimal.valueOf(
                    amount / shopAction.shopItem().itemStack().getAmount() * shopAction.itemCountSells(player)
            ).setScale(2, RoundingMode.HALF_DOWN).doubleValue();
        };

        var curr = CoinsEngineAPI.getCurrency(currency);
        if (curr == null) {
            return false;
        }
        return CoinsEngineAPI.getBalance(player, curr) >= actualAmount;
    }

    @Override
    public boolean isAvailable(MenuShopsPlugin plugin) {
        if (!plugin.pluginHookService().isAvailable(CoinsEngineHook.class)) {
            return false;
        }
        var curr = CoinsEngineAPI.getCurrency(currency);
        return curr != null;
    }

    @Override
    public NodePath formatNode() {
        return NodePath.path("gui", "value-format", "coinsengine", currency);
    }

    public String currency() {
        return currency;
    }
}
