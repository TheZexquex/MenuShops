package dev.thezexquex.menushops.shop.value;

import org.bukkit.entity.Player;

public abstract class Value {
    protected final int amount;

    public Value(int amount) {
        this.amount = amount;
    }

    public abstract void withdraw(Player player, boolean stack);
    public abstract void deposit(Player player, boolean stack);
    public abstract boolean hasEnough(Player player, boolean stack);

    public int amount() {
        return amount;
    }
}
