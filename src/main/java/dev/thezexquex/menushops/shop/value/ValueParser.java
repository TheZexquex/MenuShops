package dev.thezexquex.menushops.shop.value;

import dev.thezexquex.menushops.hooks.PluginHookService;
import dev.thezexquex.menushops.hooks.externalhooks.CoinsEngineHook;
import dev.thezexquex.menushops.shop.value.values.CoinsEngineValue;
import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import dev.thezexquex.menushops.shop.value.values.VaultValue;
import dev.thezexquex.menushops.util.KeyValue;
import org.bukkit.Material;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValueParser {

    public static Value fromPattern(String valuePattern) {
        var type = valuePattern.split("#")[0];

        var amount = Integer.parseInt(valuePattern.split("#")[1].split(":")[1]);

        if (type.equals("vault")) {
            return new VaultValue(amount);
        }
        if (type.equals("coinsengine")) {
            var currency = valuePattern.split("#")[1].split(":")[0];
            return new CoinsEngineValue(amount, currency);
        }

        var material = Material.valueOf(valuePattern.split("#")[1].split(":")[0].toUpperCase());
        return new MaterialValue(amount, material);
    }

    public static String toPattern(Value value) {
        if (value instanceof MaterialValue materialValue){
            return "material#" + materialValue.material().name().toLowerCase() + ":" + value.amount();
        } else if (value instanceof CoinsEngineValue coinsEngineValue){
            return "coinsengine#" + coinsEngineValue.currency() + ":" + coinsEngineValue.amount;
        } else {
            return "vault#money:" + value.amount();
        }
    }

    public static ValueParserResult validate(String valuePattern, PluginHookService pluginHookService) {
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
                if (!pluginHookService.isAvailable(CoinsEngineHook.class)) {
                    return new ValueParserResult(ValueParserResultType.HOOK_NOT_AVAILABLE, 0, type.length());
                }

                var potentialCurrency = valuePattern.split("#")[1].split(":")[0];

                var currency = CoinsEngineAPI.getCurrency(potentialCurrency);

                if (currency == null) {
                    return new ValueParserResult(ValueParserResultType.INVALID_CURRENCY, valuePattern.indexOf(potentialCurrency), potentialCurrency.length());
                }

                var possibleAmount = valuePattern.split(":")[1];

                int errorPositionEnd = valuePattern.indexOf(possibleAmount) + possibleAmount.length();

                try{
                    var amount = Integer.parseInt(possibleAmount);
                    if (!(amount >= 0)) {
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
        INVALID_VALUE_TYPE,
        INVALID_MATERIAL_VALUE,
        INVALID_AMOUNT_SIZE,
        INVALID_SYNTAX,
        INVALID_CURRENCY,
        AMOUNT_NO_NUMBER,
        HOOK_NOT_AVAILABLE,
        VALID
    }
}
