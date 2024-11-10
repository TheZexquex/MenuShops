package dev.thezexquex.menushops.shop.value.type;

import dev.thezexquex.menushops.command.argument.NumberSuggestionContext;
import dev.thezexquex.menushops.shop.value.Value;

import java.util.Collection;

public interface ValueType {
    Class<? extends Value> valueClass();

    String identifier();

    Collection<String> suggestions(String token);
    Collection<String> suggestions();
    Collection<String> amountSuggestions(String token, NumberSuggestionContext context);
    Collection<String> amountSuggestions();
}
