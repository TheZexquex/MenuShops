package dev.thezexquex.menushops.shop.value;

import dev.thezexquex.menushops.MenuShopsPlugin;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public abstract class Value<T> {
    protected final T amount;

    public Value(T amount) {
        this.amount = amount;
    }

    public abstract void withdraw(Player player, MenuShopsPlugin plugin);
    public abstract void deposit(Player player, MenuShopsPlugin plugin);
    public abstract boolean hasEnough(Player player, MenuShopsPlugin plugin);
    public abstract boolean isAvailable(MenuShopsPlugin plugin);
    public abstract NodePath formatNode();
    public T amount() {
        return amount;
    }
}
