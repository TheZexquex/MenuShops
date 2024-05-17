package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.shop.value.Value;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.spongepowered.configurate.NodePath;

public class MaterialValue extends Value {
    private final Material material;

    public MaterialValue(int amount, Material material) {
        super(amount);
        this.material = material;
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
        return NodePath.path("gui", "value-format", "material");
    }

    public Material material() {
        return material;
    }
}
