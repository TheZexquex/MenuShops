package dev.thezexquex.menushops.shop.value;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.shop.ShopAction;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public abstract class Value {
    protected final double amount;

    public Value(double amount) {
        this.amount = amount;
    }

    public abstract void withdraw(Player player, MenuShopsPlugin plugin, ShopAction action);
    public abstract void deposit(Player player, MenuShopsPlugin plugin, ShopAction shopAction);
    public abstract boolean hasEnough(Player player, MenuShopsPlugin plugin, ShopAction shopAction);
    public abstract boolean isAvailable(MenuShopsPlugin plugin);
    public abstract NodePath formatNode();
    public double amount() {
        return amount;
    }
}
