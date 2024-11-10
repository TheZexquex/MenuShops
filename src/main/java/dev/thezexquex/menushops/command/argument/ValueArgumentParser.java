package dev.thezexquex.menushops.command.argument;

import dev.thezexquex.menushops.command.AdditionalCaptionKeys;
import dev.thezexquex.menushops.hooks.PluginHookRegistry;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
import dev.thezexquex.menushops.shop.value.ValueRegistry;
import dev.thezexquex.menushops.shop.value.type.ValueType;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;

import java.util.*;

public class ValueArgumentParser<C> implements ArgumentParser<C, Value>, BlockingSuggestionProvider.Strings<C> {
    private final PluginHookRegistry pluginHookRegistry;
    private final ValueRegistry valueRegistry;
    private final Map<String, Iterable<String>> suggestions = new HashMap<>();

    private ValueArgumentParser(PluginHookRegistry pluginHookRegistry, ValueRegistry valueRegistry) {
        this.pluginHookRegistry = pluginHookRegistry;
        this.valueRegistry = valueRegistry;
    }

    private static final String CURR_SEP = "#";
    private static final String AMOU_SEP = ":";

    @Override
    public @NonNull ArgumentParseResult<@NonNull Value> parse(
            @NonNull CommandContext<@NonNull C> commandContext,
            @NonNull CommandInput commandInput
    ) {
        var input = commandInput.peekString();

        var result = ValueParser.validate(input, pluginHookRegistry);

        if (result.valueParserResultType() == ValueParser.ValueParserResultType.VALID) {
            commandInput.readString();
            return ArgumentParseResult.success(ValueParser.fromPattern(input));
        }

        return ArgumentParseResult.failure(
                new ValueArgumentParserException(
                        getClass(),
                        commandContext,
                        AdditionalCaptionKeys.ARGUMENT_PARSE_FAILURE_VALUE,
                        CaptionVariable.of("error-mark", buildErrorMark(input, result))
                )
        );
    }

    @Override
    public @NonNull Iterable<@NonNull String> stringSuggestions(@NonNull CommandContext<C> commandContext, @NonNull CommandInput input) {
        var token = input.readString();

        System.out.println("'" + token + "'");

        for (ValueType valueType : valueRegistry.getRegistered()) {
            var splitToken = token.split(CURR_SEP);
            if (splitToken.length == 2) {
                if (valueType.suggestions().contains(splitToken[1])) {
                    if (!token.contains(AMOU_SEP)) {
                        return List.of(token + AMOU_SEP);
                    }
                }

                if (token.contains(AMOU_SEP)) {
                    var isFirst = token.endsWith(AMOU_SEP);
                    var isDecimalPlace = false;
                    var splitTokenAmount = token.split(AMOU_SEP);
                    if (splitTokenAmount.length == 2 && splitTokenAmount[1].contains(".")) {
                        isDecimalPlace = true;
                    }
                    return valueType.amountSuggestions(token, new NumberSuggestionContext(isFirst, isDecimalPlace));
                }
            }
        }

        if (token.contains(CURR_SEP) && !token.contains(AMOU_SEP)) {
            var suggestions = new ArrayList<String>();
            valueRegistry.getRegistered().stream()
                    .filter(valueType -> valueType.identifier().equals(token.split(CURR_SEP)[0]))
                    .forEach(value -> suggestions.addAll(value.suggestions(token.split(CURR_SEP)[0] + CURR_SEP)));
            return suggestions;
        }

        if (valueRegistry.getRegistered().stream().anyMatch(valueType -> valueType.identifier().equals(token))) {
            return List.of(token + CURR_SEP);
        }

        return valueRegistry.getRegistered().stream().map(ValueType::identifier).toList();
    }

    public static String buildErrorMark(String input, ValueParser.ValueParserResult valueParserResult) {
        var markLength = input.length();
        var start = valueParserResult.errorPositionStart();
        var end = valueParserResult.errorPositionEnd();

        var markString = new StringBuilder();

        for (int i = 0; i < markLength; i++) {
            if (i < start || i > end - 1) {
                markString.append("<gray>").append(input.charAt(i));
            } else {
                markString.append("<dark_red>").append(input.charAt(i));
            }
        }

        return markString.toString();
    }

    public static class ValueArgumentParserException extends ParserException {
        public ValueArgumentParserException(
                @NonNull Class<?> argumentParser,
                @NonNull CommandContext<?> context,
                @NonNull Caption errorCaption,
                @NonNull CaptionVariable... captionVariables
        ) {
            super(argumentParser, context, errorCaption, captionVariables);
        }
    }

    public static ParserDescriptor<CommandSender, Value> valueParser(PluginHookRegistry pluginHookRegistry, ValueRegistry valueRegistry) {
        return ParserDescriptor.of(new ValueArgumentParser<>(pluginHookRegistry, valueRegistry), Value.class);
    }
}
