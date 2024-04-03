package dev.thezexquex.menushops.shop.value;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MaterialValue extends Value {
    private final Material material;

    public MaterialValue(int amount, Material material) {
        super(amount);
        this.material = material;
    }

    @Override
    void withdraw(Player player, boolean stack) {

    }

    @Override
    void deposit(Player player, boolean stack) {

    }

    @Override
    boolean hasEnough(Player player, boolean stack) {
        return false;
    }

    public Material material() {
        return material;
    }
}
