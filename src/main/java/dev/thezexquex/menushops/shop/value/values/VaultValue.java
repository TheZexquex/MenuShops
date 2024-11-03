package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.hooks.externalhooks.VaultHook;
import dev.thezexquex.menushops.shop.value.Value;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class VaultValue extends Value<Double> {

    public VaultValue(double amount) {
        super(amount);
    }

    @Override
    public void withdraw(Player player, MenuShopsPlugin plugin) {
        plugin.vaultEconomy().withdrawPlayer(player, amount);
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin) {
        plugin.vaultEconomy().depositPlayer(player, amount);
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin) {
        return plugin.vaultEconomy().has(player, amount);
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
