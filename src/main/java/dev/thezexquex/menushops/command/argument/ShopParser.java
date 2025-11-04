package dev.thezexquex.menushops.command.argument;

import dev.thezexquex.menushops.MenuShopsPlugin;
import dev.thezexquex.menushops.command.AdditionalCaptionKeys;
import dev.thezexquex.menushops.data.ShopService;
import dev.thezexquex.menushops.shop.MenuShop;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.parser.PlayerParser;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.concurrent.CompletableFuture;

public class ShopParser<C> implements ArgumentParser<C, MenuShop> {

    private final MenuShopsPlugin plugin;

    public ShopParser(MenuShopsPlugin plugin) {
        this.plugin = plugin;
    }

    public static <C> ParserDescriptor<C, MenuShop> shopParser(MenuShopsPlugin plugin) {
        return ParserDescriptor.of(new ShopParser<>(plugin), MenuShop.class);
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull MenuShop> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        var possibleShop = commandInput.readString();
        var shopOpt = plugin.shopService().getShop(possibleShop);
        return shopOpt.map(ArgumentParseResult::success).orElseGet(() ->
                ArgumentParseResult.failure(new ShopParseException(possibleShop, commandContext)));
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return (context, input) ->
                CompletableFuture.completedFuture(plugin.shopService().loadedShopNames().stream().map(Suggestion::suggestion).toList());
    }

    public static final class ShopParseException extends ParserException {


        private ShopParseException(
                final @NonNull String input,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    PlayerParser.class,
                    context,
                    AdditionalCaptionKeys.ARGUMENT_PARSE_FAILURE_SHOP,
                    CaptionVariable.of("shop", input)
            );
        }
    }
}
