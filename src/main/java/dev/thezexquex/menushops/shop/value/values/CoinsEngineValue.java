package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.externalhooks.CoinsEngineHook;
import dev.thezexquex.menushops.shop.ShopAction;
import dev.thezexquex.menushops.shop.ShopItem;
import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

public class CoinsEngineValue extends Value {
    private final String currency;
    public CoinsEngineValue(double amount, String currency) {
        super(amount);
        this.currency = currency;
    }

    @Override
    public void withdraw(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValueSells(amount, player, plugin);

        var curr = CoinsEngineAPI.getCurrency(currency);
        if (curr != null) {
            CoinsEngineAPI.removeBalance(player, curr, actualAmount);
        }
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var curr = CoinsEngineAPI.getCurrency(currency);

        var actualAmount = shopAction.combinedValueBuys(amount, player);

        if (curr != null) {
            CoinsEngineAPI.addBalance(player, curr, actualAmount);
        }
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValueSells(amount, player, plugin);

        var curr = CoinsEngineAPI.getCurrency(currency);
        if (curr == null) {
            return false;
        }
        return CoinsEngineAPI.getBalance(player, curr) >= actualAmount;
    }

    @Override
    public int maxAmountCanAfford(Player player, MenuShopsPlugin plugin) {
        var curr = CoinsEngineAPI.getCurrency(currency);
        if (curr == null) {
            return 0;
        }
        return (int) (CoinsEngineAPI.getBalance(player, curr) / amount());
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
