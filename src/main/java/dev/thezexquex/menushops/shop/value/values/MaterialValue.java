package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.util.InventoryUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.NodePath;

public class MaterialValue extends Value {
    private final Material material;

    public MaterialValue(int amount, Material material) {
        super(amount);
        this.material = material;
    }

    @Override
    public void withdraw(Player player, boolean stack) {
        InventoryUtil.removeSpecificItemCount(player, new ItemStack(material), amount);
    }

    @Override
    public void deposit(Player player, boolean stack) {
        player.getInventory().addItem(new ItemStack(material, amount));
    }

    @Override
    public boolean hasEnough(Player player, boolean stack) {
        return InventoryUtil.hasEnoughItems(player, new ItemStack(material), amount);
    }

    @Override
    public NodePath formatNode() {
        return NodePath.path("gui", "value-format", "material");
    }

    public Material material() {
        return material;
    }
}
