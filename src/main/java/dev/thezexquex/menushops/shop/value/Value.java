package dev.thezexquex.menushops.shop.value;

import org.bukkit.entity.Player;

public abstract class Value {
    protected final int amount;

    public Value(int amount) {
        this.amount = amount;
    }

    abstract void withdraw(Player player, boolean stack);
    abstract void deposit(Player player, boolean stack);
    abstract boolean hasEnough(Player player, boolean stack);

    public int amount() {
        return amount;
    }
}
