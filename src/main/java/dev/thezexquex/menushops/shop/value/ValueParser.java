package dev.thezexquex.menushops.shop.value;

import org.bukkit.Material;

public class ValueParser {

    public static Value fromPattern(String valuePattern) {
        var parts = valuePattern.split(":");
        var type = parts[0];

        var amount = Integer.parseInt(parts[1]);

        if (type.equals("vault")) {
            return new VaultValue(amount);
        }

        var material = Material.valueOf(type.split("_")[1]);
        return new MaterialValue(amount, material);
    }

    public static String toPatterm(Value value) {
        if (value instanceof MaterialValue materialValue){
            return "material_" + materialValue.material().name() + ":" + value.amount();
        } else {
            return "vault:" + value.amount();
        }
    }
}
