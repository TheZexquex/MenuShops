package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.externalhooks.VaultHook;
import dev.thezexquex.menushops.shop.ShopAction;
import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class VaultValue extends Value {

    public VaultValue(double amount) {
        super(amount);
    }

    @Override
    public void withdraw(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValueSells(amount, player);

        plugin.vaultEconomy().withdrawPlayer(player, actualAmount);
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValueBuys(amount, player);

        plugin.vaultEconomy().depositPlayer(player, actualAmount);
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin, ShopAction shopAction) {
        var actualAmount = shopAction.combinedValueSells(amount, player);

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