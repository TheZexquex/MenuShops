package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class CoinsEngineValue extends Value {
    private final String currency;
    public CoinsEngineValue(int amount, String currency) {
        super(amount);
        this.currency = currency;
    }

    @Override
    public void withdraw(Player player, boolean stack) {

    }

    @Override
    public void deposit(Player player, boolean stack) {

    }

    @Override
    public boolean hasEnough(Player player, boolean stack) {
        return false;
    }

    @Override
    public NodePath formatNode() {
        return NodePath.path("gui", "value-format", "coinsengine", currency);
    }

    public String currency() {
        return currency;
    }
}
