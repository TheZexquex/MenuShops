package dev.thezexquex.menushops.shop.value;

import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import dev.thezexquex.menushops.shop.value.values.VaultValue;
import dev.thezexquex.menushops.util.KeyValue;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValueParser {

    public static Value fromPattern(String valuePattern) {
        var parts = valuePattern.split(":");
        var type = parts[0];

        var amount = Integer.parseInt(parts[1]);

        if (type.equals("vault#money")) {
            return new VaultValue(amount);
        }

        var material = Material.valueOf(type.split("#")[1].toUpperCase());
        return new MaterialValue(amount, material);
    }

    public static String toPattern(Value value) {
        if (value instanceof MaterialValue materialValue){
            return "material#" + materialValue.material().name().toLowerCase() + ":" + value.amount();
        } else {
            return "vault#money:" + value.amount();
        }
    }

    public static ValueParserResult validate(String valuePattern) {
        var type = valuePattern.split("#")[0];

        switch (type) {
            case "material" -> {
                var materialType = valuePattern.split("#")[1].split(":")[0];
                if (Arrays.stream(Material.values()).noneMatch(material -> material.name().equals(materialType.toUpperCase()))) {
                    return new ValueParserResult(
                            ValueParserResultType.INVALID_MATERIAL_VALUE,
                            valuePattern.indexOf(materialType),
                            materialType.length() + valuePattern.indexOf(materialType)
                    );
                }
                var possibleAmount = valuePattern.split(":")[1];

                int errorPositionEnd = valuePattern.indexOf(possibleAmount) + possibleAmount.length();
                try{
                    var amount = Integer.parseInt(possibleAmount);
                    if (!(amount >= 0 && amount <= 64)) {
                        return new ValueParserResult(
                                ValueParserResultType.INVALID_AMOUNT_SIZE,
                                valuePattern.indexOf(possibleAmount),
                                errorPositionEnd
                        );
                    }

                    return new ValueParserResult(
                            ValueParserResultType.VALID,
                            0,
                            0
                    );
                } catch (NumberFormatException e) {
                    return new ValueParserResult(
                            ValueParserResultType.AMOUNT_NO_NUMBER,
                            valuePattern.indexOf(possibleAmount),
                            errorPositionEnd
                    );
                }
            }
            case "vault" -> {
                return new ValueParserResult(ValueParserResultType.INVALID_VALUE_TYPE, 0, type.length());
            }
            case "coinsengine" -> {
                return new ValueParserResult(ValueParserResultType.INVALID_VALUE_TYPE, 0, type.length());
            }
            default -> {
                return new ValueParserResult(ValueParserResultType.INVALID_VALUE_TYPE, 0, type.length());
            }
        }
    }

    public record ValueParserResult(
         ValueParserResultType valueParserResultType,
         int errorPositionStart,
         int errorPositionEnd
    ) {
    }


    public enum ValueParserResultType {
        INVALID_VALUE_TYPE, INVALID_MATERIAL_VALUE, INVALID_AMOUNT_SIZE, INVALID_SYNTAX, VALID, AMOUNT_NO_NUMBER
    }
}
