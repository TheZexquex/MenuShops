package dev.thezexquex.menushops.command;

import dev.thezexquex.menushops.hooks.PluginHookService;
import dev.thezexquex.menushops.shop.value.Value;
import dev.thezexquex.menushops.shop.value.ValueParser;
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

public class ValueArgumentParser<C> implements ArgumentParser<C, Value> {
    private final PluginHookService pluginHookService;

    public ValueArgumentParser(PluginHookService pluginHookService) {
        this.pluginHookService = pluginHookService;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Value> parse(
            @NonNull CommandContext<@NonNull C> commandContext,
            @NonNull CommandInput commandInput
    ) {
        var input = commandInput.peekString();

        var result = ValueParser.validate(input, pluginHookService);

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

    public static ParserDescriptor<CommandSender, Value> valueParser(PluginHookService pluginHookService) {
        return ParserDescriptor.of(new ValueArgumentParser<>(pluginHookService), Value.class);
    }
}
