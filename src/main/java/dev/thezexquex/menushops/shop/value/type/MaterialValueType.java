package dev.thezexquex.menushops.shop.value.type;

import dev.thezexquex.menushops.command.argument.NumberSuggestionContext;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.values.MaterialValue;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class MaterialValueType implements ValueType{
    private static final int MIN_SUGGESTION = 0;
    private static final int MAX_SUGGESTION = 10;

    @Override
    public Class<? extends Value> valueClass() {
        return MaterialValue.class;
    }

    @Override
    public String identifier() {
        return "material";
    }

    @Override
    public Collection<String> suggestions() {
        return Arrays.stream(Material.values()).map(material -> material.name().toLowerCase()).toList();
    }

    @Override
    public Collection<String> suggestions(String token) {
        return suggestions().stream().map(suggestion -> token + suggestion).toList();
    }

    @Override
    public Collection<String> amountSuggestions() {
        return IntStream.range(MIN_SUGGESTION, MAX_SUGGESTION)
                .boxed()
                .sorted()
                .map(String::valueOf)
                .toList();
    }

    public Collection<String> amountSuggestions(String token) {
        return amountSuggestions().stream().map(suggestion -> token + suggestion).toList();
    }

    @Override
    public Collection<String> amountSuggestions(String token, NumberSuggestionContext context) {
        return amountSuggestions(token);
    }
}
