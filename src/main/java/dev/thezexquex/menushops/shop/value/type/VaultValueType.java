package dev.thezexquex.menushops.shop.value.type;

import com.google.common.collect.Iterables;
import dev.thezexquex.menushops.command.argument.NumberSuggestionContext;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.values.VaultValue;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

public class VaultValueType implements ValueType {
    private static final int MIN_SUGGESTION = 0;
    private static final int MAX_SUGGESTION = 9;
    @Override
    public Class<? extends Value> valueClass() {
        return VaultValue.class;
    }

    @Override
    public String identifier() {
        return "vault";
    }

    @Override
    public Collection<String> suggestions(String token) {
        return suggestions().stream().map(suggestion -> token + suggestion).toList();
    }

    @Override
    public Collection<String> suggestions() {
        return List.of("money");
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
        return context.isDecimalPlace() || context.isFirst() ? amountSuggestions(token) : (Collection<String>) Iterables.concat(List.of("."), amountSuggestions(token));
    }
}
