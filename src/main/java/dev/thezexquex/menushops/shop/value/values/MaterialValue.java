package dev.thezexquex.menushops.shop.value.values;

import dev.thezexquex.menushops.MenuShopsPlugin;
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
    public void withdraw(Player player, MenuShopsPlugin plugin) {
        InventoryUtil.removeSpecificItemCount(player, new ItemStack(material), amount);
    }

    @Override
    public void deposit(Player player, MenuShopsPlugin plugin) {
        player.getInventory().addItem(new ItemStack(material, amount));
    }

    @Override
    public boolean hasEnough(Player player, MenuShopsPlugin plugin) {
        return InventoryUtil.hasEnoughItems(player, new ItemStack(material), amount);
    }

    @Override
    public boolean isAvailable(MenuShopsPlugin plugin) {
        return true;
    }

    @Override
    public NodePath formatNode() {
        return NodePath.path("gui", "value-format", "material");
    }

    public Material material() {
        return material;
    }
}
